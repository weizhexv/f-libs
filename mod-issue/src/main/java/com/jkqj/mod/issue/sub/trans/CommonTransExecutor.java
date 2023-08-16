package com.jkqj.mod.issue.sub.trans;

public interface CommonTransExecutor {

    void doTrans(TransCallback transCallback);

    <T> T doTrans(TransCallbackResult<T> transCallback);
}
