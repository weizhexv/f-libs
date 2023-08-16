package com.jkqj.nats;

import io.nats.client.Message;
import io.nats.client.*;
import io.nats.client.support.Status;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NatsErrorListener implements ErrorListener {
    @Override
    public void errorOccurred(Connection conn, String error) {
        log.error("error occurred {}", error);
    }

    @Override
    public void exceptionOccurred(Connection conn, Exception exp) {
        log.error("exception occurred", exp);
    }

    @Override
    public void slowConsumerDetected(Connection conn, Consumer consumer) {
        log.info("connection slow consumer detected");
    }

    @Override
    public void messageDiscarded(Connection conn, Message msg) {
        log.info("message discarded {}", msg.getSubject());
    }

    @Override
    public void heartbeatAlarm(Connection conn, JetStreamSubscription sub, long lastStreamSequence, long lastConsumerSequence) {
        log.info("heartbeat alarm");
    }

    @Override
    public void unhandledStatus(Connection conn, JetStreamSubscription sub, Status status) {
        log.info("unhandled status {}", status);
    }

    @Override
    public void flowControlProcessed(Connection conn, JetStreamSubscription sub, String subject, FlowControlSource source) {
        log.info("flow control processed {}", subject);
    }
}
