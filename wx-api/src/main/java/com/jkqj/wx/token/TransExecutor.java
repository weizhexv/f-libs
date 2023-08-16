package com.jkqj.wx.token;

public interface TransExecutor {
    <T> T doTrans(TransWorker<T> tTransWorker);
}
