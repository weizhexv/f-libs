package com.jkqj.mod.issue.sub.core;

import com.jkqj.mod.issue.sub.model.TargetBody;

import java.util.List;

public interface MessageHandlerDispatcher {
    void acceptHandler(MessageHandler messageHandler);

    List<String> collectHandlerName(String body);

    void targetDispatch(List<TargetBody> tbList);
}
