package com.jkqj.wx.api.token;

import com.jkqj.wx.api.WxApi;
import com.jkqj.wx.api.model.AccessTokenResult;

public interface TokenDelegate {
    long SHIFT_SECONDS = 10L;
    AccessTokenResult getToken(WxApi wxApi, String appId, String secret, String badToken);
}
