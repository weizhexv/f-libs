package com.jkqj.nats;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Consumer;

public interface MessageClient {
    String HEADER_DELAY = "delay";
    String HEADER_PUB_AT = "pub-at";

    void publish(Subject subject, byte[] body);

    void publish(Subject subject, Map<String, String> headers, byte[] body);

    void publish(Subject subject, byte[] body, Duration delay);

    void publish(Subject subject, Map<String, String> headers, byte[] body, Duration delay);

    void subscribe(Subject subject, Consumer<Message> handler);

    void subscribeFrom(Subject subject, Duration offset, Consumer<Message> handler);

    void subscribeFrom(Subject subject, LocalDateTime startAt, Consumer<Message> handler);

    Subscriber subscribe(Subject subject);

    Subscriber subscribeFrom(Subject subject, long sequence);

    void flush(Duration duration);

    boolean unsubscribe(Subject subject);
}
