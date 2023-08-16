package com.jkqj.wx.api;

import com.jkqj.wx.api.model.AcCodeRequest;
import com.jkqj.wx.api.model.AcCodeResult;
import com.jkqj.wx.api.model.AccessTokenResult;

public interface WxApi {
    AccessTokenResult getAccessToken(String appId,String secret);

    AcCodeResult createAcCode(String accessToken, AcCodeRequest request);
}
