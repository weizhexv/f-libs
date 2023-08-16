package com.jkqj.wx.api;

import com.jkqj.common.exception.BusinessException;
import com.jkqj.common.utils.JsonUtils;
import com.jkqj.wx.api.model.AcCodeRequest;
import com.jkqj.wx.api.model.AcCodeResult;
import com.jkqj.wx.api.model.AccessTokenResult;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;

@Slf4j
public class WxApiImpl implements WxApi {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private final OkHttpClient okHttpClient;
    public final static String WX_HOST = "https://api.weixin.qq.com";

    public WxApiImpl(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    @Override
    public AccessTokenResult getAccessToken(String appId,String secret) {
        String url = String.format("%s/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s", WX_HOST, appId, secret);
        log.info("getAccessToken:{}", url);
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(url).build();
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new BusinessException("getAccessToken,call http error");
            }
            String json = response.body().string();
            return JsonUtils.toBean(json, AccessTokenResult.class);
        } catch (IOException e) {
            log.error("getAccessToken error", e);
            throw new BusinessException(e.getMessage());
        } finally {
            if (request != null) {
                response.close();
            }
        }
    }

    @Override
    public AcCodeResult createAcCode(String accessToken, AcCodeRequest request) {
        String url = String.format("%s/wxa/getwxacodeunlimit?access_token=%s", WX_HOST, accessToken);
        log.info("createAcCode:{}", url);
        RequestBody requestBody = RequestBody.create(JSON, JsonUtils.toJson(request));
        Request.Builder builder = new Request.Builder().url(url).post(requestBody);
        Response response = null;
        try {
            response = okHttpClient.newCall(builder.build()).execute();
            if (!response.isSuccessful()) {
                throw new BusinessException("createAcCode,call http error");
            }
            MediaType mediaType = response.body().contentType();
            if ("json".equalsIgnoreCase(mediaType.subtype())) {
                String json = response.body().string();
                return JsonUtils.toBean(json, AcCodeResult.class);
            }
            AcCodeResult acCodeResult = new AcCodeResult();
            acCodeResult.setErrCode(0);
            acCodeResult.setContentType(mediaType.subtype());
            acCodeResult.setBuffer(response.body().bytes());
            return acCodeResult;
        } catch (IOException e) {
            log.error("createAcCode error", e);
            throw new BusinessException(e.getMessage());
        } finally {
            if (request != null) {
                response.close();
            }
        }
    }
}
