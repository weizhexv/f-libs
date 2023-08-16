package com.jkqj.wx.token;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DefaultTransExecutor implements TransExecutor {
    @Override
    @Transactional
    public <T> T doTrans(TransWorker<T> tTransWorker) {
        return tTransWorker.doWork();
    }
}
