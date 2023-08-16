package com.jkqj.mod.issue.pub.output.mq;

import com.jkqj.dtrace.context.ReqRunContext;
import com.jkqj.mod.issue.pub.output.MsgOutput;
import com.jkqj.nats.MessageClient;
import com.jkqj.nats.Subject;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class MqMsgOutput implements MsgOutput {

    private final Subject subject;

    @Resource
    private MessageClient messageClient;

    public MqMsgOutput(Subject subject) {
        this.subject = subject;
    }

    @Override
    public int output(String message) {
        log.info("send to {},{}", subject.getId(), message);
        String traceId = ReqRunContext.getTraceId();
        if (traceId == null || traceId.length() == 0) {
            log.info("no trace id, create it");
            traceId = UUID.randomUUID().toString();
        }
        Map<String, String> headers = new HashMap<>();
        headers.put(ReqRunContext.TRACE_ID_KEY, traceId);
        messageClient.publish(subject, headers, message.getBytes(StandardCharsets.UTF_8));
        return 1;
    }
}
