package com.jkqj.nats;

import java.time.Duration;
import java.util.List;

public interface Subscriber {
    List<Message> fetch(int batchSize, Duration timeout);
}
