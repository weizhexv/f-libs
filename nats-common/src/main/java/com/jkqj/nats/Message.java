package com.jkqj.nats;

import io.nats.client.impl.AckType;

import java.time.Duration;
import java.util.Map;

public interface Message {
    byte[] getBody();

    String asString();

    String getHeader(String name);

    Map<String, String> getHeaders();

    void ack(AckType type);

    void ack();

    void nak(Duration delay);

    void nak(String delay);

    long sequence();

    boolean acked();
}
