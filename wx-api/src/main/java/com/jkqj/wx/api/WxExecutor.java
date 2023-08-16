package com.jkqj.wx.api;

public interface WxExecutor<T extends WxErrCode> {
    T execute(String wxToken);
    default boolean invalidWxToken(long errCode) {
        return errCode == 40001L;
    }
}
