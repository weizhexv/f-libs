package com.jkqj.wx.api;

import com.jkqj.common.exception.BusinessException;
import com.jkqj.wx.api.model.AcCodeRequest;
import com.jkqj.wx.api.model.AcCodeResult;
import com.jkqj.wx.api.model.AccessTokenResult;
import com.jkqj.wx.api.token.TokenDelegate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


@Slf4j
public class WxFeature {
    private final WxApi wxApi;
    private final String appId;
    private final String secret;
    private TokenDelegate tokenDelegate;


    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    private TokenContext tokenContext = new TokenContext(null, 0);

    private int retries = 2;

    private static class TokenContext {
        private final AccessTokenResult currentToken;
        private final long expireAt;

        private TokenContext(AccessTokenResult currentToken, long expireAt) {
            this.currentToken = currentToken;
            this.expireAt = expireAt;
        }

        boolean isValid() {
            if (currentToken != null && System.currentTimeMillis() < expireAt) {
                return true;
            }
            return false;
        }
    }

    public WxFeature(WxApi wxApi, TokenDelegate tokenDelegate, String appId, String secret) {
        this.wxApi = wxApi;
        this.appId = appId;
        this.secret = secret;
        this.tokenDelegate = tokenDelegate;
    }

    public void forceRefreshToken(String badToken) {
        if (StringUtils.isBlank(badToken)) {
            //
            log.warn("invalid bad token.");
            return;
        }
        writeLock.lock();
        try {
            refreshTokenNow(badToken);
        } finally {
            writeLock.unlock();
        }
    }

    public AccessTokenResult getAccessToken() {
        return ensureAccessTokenResult();
    }

    public <T extends WxErrCode> T wxExecuteTemplate(WxExecutor<T> executor, int bizRetries) {
        if (bizRetries <= 1) {
            bizRetries = 1;
        }
        while (true) {
            AccessTokenResult token = ensureAccessTokenResult();
            T result = executor.execute(token.getAccessToken());
            if (bizRetries > 0 &&  executor.invalidWxToken(result.getErrCode())) {
                bizRetries--;
                log.warn("force reset token.");
                forceRefreshToken(token.getAccessToken());
                continue;
            }
            return result;
        }
    }


    public AcCodeResult createAcCode(AcCodeRequest request) {
        return createAcCode(request, 1);
    }

    public AcCodeResult createAcCode(AcCodeRequest request, int bizRetries) {
        if (request.getWidth() < 280 || request.getWidth() > 1280) {
            throw new BusinessException("大小应该在280-1280之间");
        }
        if (!"release".equals(request.getEnvVersion())
                && "trial".equals(request.getEnvVersion())
                && "develop".equals(request.getEnvVersion())) {
            throw new BusinessException("envVersion应该是: release|trial|develop之一");
        }

        return wxExecuteTemplate(wxToken -> wxApi.createAcCode(wxToken, request), bizRetries);
    }



    private AccessTokenResult ensureAccessTokenResult() {
        readLock.lock();
        try {
            if (tokenContext.isValid()) {
                log.info("get wx token from memory.");
                return tokenContext.currentToken;
            }
        } finally {
            readLock.unlock();
        }
        // get from wx
        writeLock.lock();
        try {
            if (tokenContext.isValid()) {
                return tokenContext.currentToken;
            }
            return refreshTokenNow(null);
        } finally {
            writeLock.unlock();
        }
    }

    private AccessTokenResult refreshTokenNow(String badToken) {
        int counter = 0;
        while (counter < retries) {
            try {
                AccessTokenResult tokenResult = tokenDelegate.getToken(wxApi, appId, secret, badToken);
                if (tokenResult.getErrCode() == 0) {
                    this.tokenContext = new TokenContext(tokenResult, tokenResult.getExpiresIn());
                    return tokenResult;
                } else {
                    counter++;
                    log.warn("get token error:{}", counter);
                }
            } catch (RuntimeException e) {
                log.warn("get token met exp:{}", counter, e);
                counter++;
            }
        }
        throw new BusinessException("get token error");
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }
}
