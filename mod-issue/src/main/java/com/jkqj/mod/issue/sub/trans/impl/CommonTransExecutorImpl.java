package com.jkqj.mod.issue.sub.trans.impl;

import com.jkqj.mod.issue.sub.trans.CommonTransExecutor;
import com.jkqj.mod.issue.sub.trans.TransCallback;
import com.jkqj.mod.issue.sub.trans.TransCallbackResult;
import org.springframework.transaction.annotation.Transactional;


/**
 * 事务
 *
 * @author liuyang
 */
public class CommonTransExecutorImpl implements CommonTransExecutor {

    /**
     * 事务
     */
    @Transactional
    @Override
    public void doTrans(TransCallback transCallback) {
        transCallback.callback();
    }

    /**
     * 事务（返回值）
     */
    @Transactional
    @Override
    public <T> T doTrans(TransCallbackResult<T> transCallback) {
        return transCallback.callback();
    }
}