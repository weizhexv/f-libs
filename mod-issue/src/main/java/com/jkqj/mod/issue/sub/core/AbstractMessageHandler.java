package com.jkqj.mod.issue.sub.core;

import com.jkqj.mod.issue.msg.IssueMessage;
import com.jkqj.mod.issue.sub.dal.mapper.IssueSubMapper;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

public abstract class AbstractMessageHandler implements MessageHandler {

    @Resource
    private MessageHandlerDispatcher messageHandlerDispatcher;

    @Resource
    private IssueSubMapper issueSubMapper;


    @PostConstruct
    public void init() {
        messageHandlerDispatcher.acceptHandler(this);
    }

    protected void complete(Long taskId){
        // 更改消息处理状态
        issueSubMapper.changeStatus(taskId);
    }

    @Override
    public void handle(Long taskId, String topic, IssueMessage message){
        processMsg(topic, message);
        complete(taskId);
    }

    protected abstract void processMsg(String topic, IssueMessage message);
}
