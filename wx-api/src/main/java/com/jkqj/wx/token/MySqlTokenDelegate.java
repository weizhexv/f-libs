package com.jkqj.wx.token;

import com.jkqj.wx.api.WxApi;
import com.jkqj.wx.api.model.AccessTokenResult;
import com.jkqj.wx.api.token.TokenDelegate;
import com.jkqj.wx.token.dal.mapper.WxTokenMapper;
import com.jkqj.wx.token.dal.po.WxToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
public class MySqlTokenDelegate implements TokenDelegate {
    @Resource
    private WxTokenMapper wxTokenMapper;

    @Resource
    private TransExecutor transExecutor;

    @Override
    public AccessTokenResult getToken(WxApi wxApi, String appId, String secret, String badToken) {
        if (StringUtils.isNotBlank(badToken)) {
            return resetToken(wxApi, appId, secret, badToken);
        }

        return normal(wxApi, appId, secret);
    }

    private AccessTokenResult normal(WxApi wxApi, String appId, String secret) {
        List<WxToken> tokenList = wxTokenMapper.selectToken(appId);
        WxToken token = tokenList.get(0);
        if (token.getExpired() > System.currentTimeMillis()) {
            log.info("direct read wx token from mysql.");
            return fromToken(token);
        }
        return resetToken(wxApi, appId, secret, null);
    }

    private AccessTokenResult resetToken(WxApi wxApi, String appId, String secret, String badToken) {
        return transExecutor.doTrans(() -> resetTokenCore(wxApi, appId, secret, badToken));
    }

    private AccessTokenResult resetTokenCore(WxApi wxApi, String appId, String secret, String badToken) {
        List<WxToken> tokenList = wxTokenMapper.selectTokenLock(appId);
        WxToken token = tokenList.get(0);
        if (token.getExpired() > System.currentTimeMillis() && !token.getToken().equals(badToken)) {
            log.info("locked and read wx token, bak:{}",badToken);
            return fromToken(token);
        }
        log.info("call wx rpc to get wx token, need to save db");
        AccessTokenResult result = wxApi.getAccessToken(appId, secret);
        if (result.getErrCode() == 0) {
            log.info("called wx rpc to get wx token, save into db");
            saveToken(result, appId);
        }
        return result;
    }

    private AccessTokenResult fromToken(WxToken token) {
        AccessTokenResult result = new AccessTokenResult();
        result.setErrCode(0);
        result.setAccessToken(token.getToken());
        result.setExpiresIn(token.getExpired());
        return result;
    }

    private int saveToken(AccessTokenResult tokenResult, String appId) {
        WxToken token = new WxToken();
        token.setAppId(appId);
        token.setToken(tokenResult.getAccessToken());
        long expired =   System.currentTimeMillis() +  (tokenResult.getExpiresIn() - SHIFT_SECONDS) * 1000L;
        token.setExpired(expired);
        return wxTokenMapper.update(token);
    }
}
