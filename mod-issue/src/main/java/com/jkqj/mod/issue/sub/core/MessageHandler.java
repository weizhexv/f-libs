package com.jkqj.mod.issue.sub.core;

import com.jkqj.mod.issue.msg.IssueMessage;

/**
 * 消息处理器
 */
public interface MessageHandler {
    String uniqueName();
    boolean interested(String eventSource);
    void handle(Long taskId, String topic, IssueMessage message);
}
