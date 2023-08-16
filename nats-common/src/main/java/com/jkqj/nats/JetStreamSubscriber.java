package com.jkqj.nats;

import io.nats.client.JetStreamSubscription;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

public class JetStreamSubscriber implements Subscriber {
    private final JetStreamSubscription subscription;

    public JetStreamSubscriber(JetStreamSubscription subscription) {
        this.subscription = subscription;
    }

    @Override
    public List<Message> fetch(int batchSize, Duration timeout) {
        checkArgument(batchSize > 0);
        checkArgument(timeout != null);

        return subscription.fetch(batchSize, timeout)
                .stream()
                .map(JetStreamMessage::new)
                .collect(Collectors.toList());
    }
}
