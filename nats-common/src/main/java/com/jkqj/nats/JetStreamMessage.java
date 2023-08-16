package com.jkqj.nats;

import io.nats.client.impl.AckType;
import io.nats.client.support.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

@Slf4j
public class JetStreamMessage implements Message {
    private final io.nats.client.Message message;
    private boolean ack;

    public JetStreamMessage(io.nats.client.Message jetMessage) {
        this.message = jetMessage;
    }

    public static Message from(io.nats.client.Message jetMessage) {
        return new JetStreamMessage(jetMessage);
    }

    @Override
    public byte[] getBody() {
        return message.getData();
    }

    @Override
    public String asString() {
        var data = message.getData();
        return new String(data, StandardCharsets.UTF_8);
    }

    @Override
    public String getHeader(String name) {
        if (message.getHeaders().containsKeyIgnoreCase(name)) {
            return message.getHeaders().getFirst(name);
        }

        return null;
    }

    @Override
    public Map<String, String> getHeaders() {
        var headers = message.getHeaders();
        if (headers == null || headers.isEmpty()) {
            return Collections.emptyMap();
        }

        var map = new HashMap<String, String>(headers.size());
        for (var key : headers.keySet()) {
            map.put(key, headers.getFirst(key));
        }

        return map;
    }

    @Override
    public void ack(AckType type) {
        if (ack) {
            return;
        }

        message.ack();
        ack = true;
    }

    @Override
    public void ack() {
        ack(AckType.AckAck);
    }

    @Override
    public void nak(Duration delay) {
        checkArgument(delay != null);
        if (message.getSubscription() == null) {
            throw new IllegalStateException("Message is not bound to a subscription.");
        }

        var type = AckType.AckNak.bytes;
        var space = " ".getBytes();
        var args = JsonUtils.simpleMessageBody("delay", delay.toNanos());
        var bytes = new byte[type.length + space.length + args.length];
        System.arraycopy(type, 0, bytes, 0, type.length);
        System.arraycopy(space, 0, bytes, type.length, space.length);
        System.arraycopy(args, 0, bytes, type.length + space.length, args.length);

        log.debug("reply to {}", message.getReplyTo());
        log.debug("nak {}", new String(bytes));
        message.getConnection().publish(message.getReplyTo(), bytes);
    }

    @Override
    public void nak(String delay) {
        checkArgument(StringUtils.isNumeric(delay));
        nak(Duration.ofMillis(Long.parseLong(delay)));
    }

    @Override
    public long sequence() {
        return message.metaData().streamSequence();
    }

    @Override
    public boolean acked() {
        return ack;
    }
}
