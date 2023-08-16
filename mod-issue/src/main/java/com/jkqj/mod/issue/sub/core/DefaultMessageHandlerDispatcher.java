package com.jkqj.mod.issue.sub.core;

import com.jkqj.common.utils.JsonUtils;
import com.jkqj.mod.issue.msg.IssueMessage;
import com.jkqj.mod.issue.sub.executor.IssueSubExecutor;
import com.jkqj.mod.issue.sub.dal.mapper.IssueSubMapper;
import com.jkqj.mod.issue.sub.executor.IssueSubWaitException;
import com.jkqj.mod.issue.sub.model.TargetBody;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;


/**
 * 默认消息调度器
 */
@Slf4j
public class DefaultMessageHandlerDispatcher implements MessageHandlerDispatcher {

    @Resource
    private IssueSubMapper issueSubMapper;

    private final Map<String, MessageHandler> handlerMap = new ConcurrentHashMap<>();

    /**
     * 初始化时注入
     */
    @Override
    public void acceptHandler(MessageHandler messageHandler) {
        // 组建 处理器名称为Key Handler为value的Map
        String name = messageHandler.uniqueName();
        MessageHandler old = handlerMap.putIfAbsent(name, messageHandler);
        if (old != null) {
            log.error("message handler repeat,name={},current class={},old class={}", name, messageHandler.getClass().getName(), old.getClass().getName());
            throw new RuntimeException("message handler repeat");
        }
    }

    /**
     * 收集Handler名称
     */
    @Override
    public List<String> collectHandlerName(String body) {
        // 1. 处理消息体
        IssueMessage issueMessage = parse(body);
        List<String> nameList = new ArrayList<>();
        for (Map.Entry<String, MessageHandler> entry : handlerMap.entrySet()) {
            if (entry.getValue().interested(issueMessage.getSource())) {
                nameList.add(entry.getKey());
            }
        }
        return nameList;
    }


    /**
     * 分发Handler处理
     */
    @Override
    public void targetDispatch(List<TargetBody> tbList) {
        for (TargetBody tb : tbList) {
            MessageHandler handler = handlerMap.get(tb.getTarget());
            if (handler == null) {
                log.info("can't find message handler:{},body:{}", tb.getTarget(), tb.getBody());
                continue;
            }
            IssueMessage issueMessage = parse(tb.getBody());
            // 返回失败
            execute(() -> handler.handle(tb.getId(), tb.getTopic(), issueMessage));
        }
    }

    private IssueMessage parse(String body) {
        return JsonUtils.toBean(body, IssueMessage.class);
    }

    private void execute(Runnable runnable) {
        try {
            IssueSubExecutor.executeTask(runnable);
        } catch (RejectedExecutionException e) {
            log.info("", e);
            throw new IssueSubWaitException();
        }
    }
}
