package com.jkqj.nats;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Function;

public interface RetryMessageHandler extends Consumer<Message> {
    Logger log = LoggerFactory.getLogger(RetryMessageHandler.class);

    @Override
    default void accept(Message message) {
        var retryTimes = retryTimes();
        var lastRetryInterval = retryInterval();

        var currentTimes = 1;
        for (; currentTimes <= retryTimes; currentTimes++) {
            try {
                handle(message);
            } catch (Throwable e) {
                log.error("catch exception", e);
                if (!message.acked()) {
                    log.info("handle message error, retry times: {}, current :{}", retryTimes, currentTimes);
                }
            }

            if (message.acked()) {
                break;
            }
            sleep(lastRetryInterval);
            lastRetryInterval = retryFactor().apply(currentTimes);
        }

        if (currentTimes == retryTimes && !message.acked()) {
            log.error("can't handle message {}, retry times {}", message.sequence(), retryTimes);
            throw new RuntimeException(String.format("can't handle message %s, retry times %d", message.sequence(), retryTimes));
        }
    }

    private void sleep(Duration retryInterval) {
        try {
            Thread.sleep(retryInterval.toMillis());
        } catch (InterruptedException ex) {
            log.error("retry sleep error", ex);
        }
    }

    void handle(Message message);

    default int retryTimes() {
        return 3;
    }

    default Duration retryInterval() {
        return Duration.ofSeconds(1);
    }

    default Function<Integer, Duration> retryFactor() {
        return (currentTimes) -> {
            if (currentTimes % 2 == 1) {
                return retryInterval();
            } else {
                return retryInterval().multipliedBy(currentTimes * 10L);
            }
        };
    }
}
