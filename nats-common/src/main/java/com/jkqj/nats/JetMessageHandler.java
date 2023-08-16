package com.jkqj.nats;

import io.nats.client.Message;
import io.nats.client.MessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Consumer;

import static com.jkqj.nats.JetStreamMessageClient.HEADER_DELAY;
import static com.jkqj.nats.MessageClient.HEADER_PUB_AT;

@Slf4j
public class JetMessageHandler implements MessageHandler {
    private final Consumer<com.jkqj.nats.Message> handler;

    public JetMessageHandler(Consumer<com.jkqj.nats.Message> handler) {
        this.handler = handler;
    }

    @Override
    public void onMessage(Message message) throws InterruptedException {
        var jetMessage = JetStreamMessage.from(message);
        var delayValue = jetMessage.getHeader(HEADER_DELAY);
        if (StringUtils.isNumeric(delayValue)) {
            var pubAtValue = jetMessage.getHeader(HEADER_PUB_AT);
            log.debug("process schedule message, delayValue {}, pubAt {}", delayValue, pubAtValue);

            var delay = Long.parseLong(delayValue);
            var pubAt = Long.parseLong(pubAtValue);
            var now = System.currentTimeMillis();
            if (now <= pubAt + delay) {
                log.debug("not due, nak");
                jetMessage.nak(delayValue);
                return;
            }
        }

        this.handler.accept(jetMessage);
//        jetMessage.ack();
    }
}
