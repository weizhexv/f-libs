package com.jkqj.nats;

import io.nats.client.NUID;
import io.nats.client.PushSubscribeOptions;
import io.nats.client.api.ConsumerConfiguration;
import io.nats.client.api.DeliverPolicy;
import io.nats.client.api.StorageType;
import io.nats.client.impl.AckType;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static com.jkqj.nats.NatsJsUtils.createStreamOrUpdateSubjects;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@SpringBootTest
@ContextConfiguration
@Execution(ExecutionMode.SAME_THREAD)
public class MessageClientTest {
    @Autowired
    private MessageClient client;

    @Autowired
    private ConnectionCache connCache;

    @SneakyThrows
    @Test
    public void subTest() {
        AtomicInteger createdCount = new AtomicInteger();
        AtomicInteger updatedCount = new AtomicInteger();

        client.subscribe(TestSubjects.TEST_USER_CREATED, e -> {
            var data = e.asString();

            log.debug("got event #{} {}", e.sequence(), data);
            createdCount.getAndIncrement();
            e.ack();
        });

        client.subscribe(TestSubjects.TEST_USER_UPDATED, e -> {
            var data = e.asString();

            log.debug("got event #{} {}", e.sequence(), data);
            updatedCount.getAndIncrement();
            e.ack();
        });

        for (var i = 0; i < 10; i++) {
            publishCreatedMessage(i, TestSubjects.TEST_USER_CREATED);
            publishUpdatedMessages(i, TestSubjects.TEST_USER_UPDATED);
        }

        client.flush(Duration.ofSeconds(3));
        assertEquals(10, createdCount.get());
        assertEquals(10, updatedCount.get());
    }

    @SneakyThrows
    @Test
    public void subRetryTest() {
        AtomicInteger createdCount = new AtomicInteger();
        var retryHandler = new RetryMessageHandler() {
            private int times = 1;

            @Override
            public void handle(com.jkqj.nats.Message message) {
                var data = message.asString();

                log.debug("got event #{} {}", message.sequence(), data);

                if (times < 3) {
                    var msg = "crash " + times;
                    times++;
                    throw new RuntimeException(msg);
                } else {
                    log.debug("processed event #{} {}", message.sequence(), data);
                    createdCount.getAndIncrement();
                }
            }
        };

        client.subscribe(TestSubjects.TEST_USER_CREATED, retryHandler);

        for (var i = 0; i < 1; i++) {
            publishCreatedMessage(i, TestSubjects.TEST_USER_CREATED);
        }

        client.flush(Duration.ofSeconds(3));
        Thread.sleep(10000);
        assertEquals(1, createdCount.get());
    }

    @Test
    public void subOffsetTest() {
        AtomicInteger createdCount = new AtomicInteger();
        AtomicInteger updatedCount = new AtomicInteger();

        publishUpdatedMessages(0, TestSubjects.TEST_USER_CREATED_OFFSET);
        publishUpdatedMessages(0, TestSubjects.TEST_USER_UPDATED_OFFSET);

        client.subscribeFrom(TestSubjects.TEST_USER_CREATED_OFFSET, Duration.ofSeconds(0), e -> {
            var data = e.asString();

            log.debug("got event #{} {}", e.sequence(), data);
            createdCount.getAndIncrement();
        });

        client.subscribeFrom(TestSubjects.TEST_USER_UPDATED_OFFSET, Duration.ofSeconds(5), e -> {
            var data = e.asString();

            log.debug("got event #{} {}", e.sequence(), data);
            updatedCount.getAndIncrement();
        });

        for (var i = 0; i < 100; i++) {
            publishCreatedMessage(i, TestSubjects.TEST_USER_CREATED_OFFSET);
            publishUpdatedMessages(i, TestSubjects.TEST_USER_UPDATED_OFFSET);
        }

        client.flush(Duration.ofSeconds(3));

        log.debug("created {}", createdCount.get());
        log.debug("updated {}", updatedCount.get());

        assertEquals(100, createdCount.get());
        assertEquals(101, updatedCount.get());
    }

    @Test
    public void subStartAtTest() {
        AtomicInteger createdCount = new AtomicInteger();
        AtomicInteger updatedCount = new AtomicInteger();

        publishUpdatedMessages(0, TestSubjects.TEST_USER_CREATED);
        publishUpdatedMessages(0, TestSubjects.TEST_USER_UPDATED);

        var startAt = LocalDateTime.now().minus(Duration.ofSeconds(5));
        var testSubject = TestSubjects.TEST_USER_CREATED;
        client.subscribeFrom(testSubject, startAt, e -> {
            var data = e.asString();

            log.debug("got event {}", data);
            createdCount.getAndIncrement();
        });

        client.subscribeFrom(TestSubjects.TEST_USER_UPDATED, startAt.plus(Duration.ofSeconds(100)), e -> {
            var data = e.asString();

            log.debug("got event {}", data);
            updatedCount.getAndIncrement();
        });

        client.flush(Duration.ofSeconds(1));

        for (var i = 0; i < 10; i++) {
            publishCreatedMessage(i, TestSubjects.TEST_USER_CREATED);
            publishUpdatedMessages(i, TestSubjects.TEST_USER_UPDATED);
        }

        client.flush(Duration.ofSeconds(3));
        assertEquals(11, createdCount.get());
        assertEquals(10, updatedCount.get());
    }

    @SneakyThrows
    @Test
    public void suGroupNatsTest() {
        var nc = connCache.conn();
        var jsm = nc.jetStreamManagement();
        var js = nc.jetStream();

        var subAbc = "sub_abc";
        var name = "su_group_nats";
        var group = "group_123";

        createStreamOrUpdateSubjects(jsm, name, StorageType.File, subAbc);

        js.publish(subAbc, ("msg: " + 0).getBytes(StandardCharsets.UTF_8));
        nc.flush(Duration.ofSeconds(1));

        var dname = "dur_xyz_" + 0;
        ConsumerConfiguration cc;
        try {
            var consumerInfo = jsm.getConsumerInfo(name, dname);
            cc = consumerInfo.getConsumerConfiguration();
        } catch (Exception e) {
            cc = ConsumerConfiguration.builder()
                    .deliverPolicy(DeliverPolicy.LastPerSubject)
                    .durable(dname)
                    .build();

        }
        var options = PushSubscribeOptions.builder()
                .stream(name)
                .durable(dname)
                .configuration(cc)
                .build();

        for (int j = 1; j <= 2; j++) {
            int finalJ = j;

            js.subscribe(subAbc, group, nc.createDispatcher(), message -> {
                log.debug("[#{}] got message {}", finalJ, new String(message.getData()));
                message.ack();
            }, false, options);
        }
        nc.flush(Duration.ofSeconds(3));

        for (int i = 1; i <= 10; i++) {
            js.publish(subAbc, ("msg: " + i).getBytes(StandardCharsets.UTF_8));
        }

        nc.flush(Duration.ofSeconds(3));
    }

    @SneakyThrows
    @Test
    public void subGroupTest() {
        final AtomicInteger count = new AtomicInteger(0);
        client.subscribeFrom(TestSubjects.TEST_USER_CREATED_GROUP, Duration.ZERO, e -> {
            var data = e.asString();

            log.debug("[A] got event {} {}", data, e.sequence());
            e.ack(AckType.AckAck);
            count.incrementAndGet();
        });

        client.subscribeFrom(TestSubjects.TEST_USER_CREATED_GROUP, Duration.ZERO, e -> {
            var data = e.asString();

            log.debug("[B] got event {} {}", data, e.sequence());
            e.ack(AckType.AckAck);
            count.incrementAndGet();
        });

        for (var i = 0; i < 10; i++) {
            publishCreatedMessage(i, TestSubjects.TEST_USER_CREATED_GROUP);
        }
        client.flush(Duration.ofSeconds(3));
        assertEquals(10, count.get());

        var unsubscribed = client.unsubscribe(TestSubjects.TEST_USER_CREATED_GROUP);
        assertTrue(unsubscribed);
    }

    @Test
    @SneakyThrows
    public void pullTest() {
        var subscriber = client.subscribe(TestSubjects.TEST_USER_CREATED_PULL);
        var times = 0;
        var count = 0;

        for (int i = 0; i < 100; i++) {
            publishCreatedMessage(i, TestSubjects.TEST_USER_CREATED_PULL);
        }

        do {
            var messages = subscriber.fetch(10, Duration.ofSeconds(3));
            log.debug("got message list size {}", messages.size());
            for (com.jkqj.nats.Message message : messages) {
                log.debug("got msg {} #{}", message.asString(), message.sequence());
                count++;
                message.ack(AckType.AckAck);
            }

        } while (times++ <= 11);

        assertEquals(100, count);
    }

    @Test
    @SneakyThrows
    public void pullFromTest() {
        var subscriber = client.subscribe(TestSubjects.TEST_USER_CREATED_PULL_FROM);
        var times = 0;
        var count = 0;

        for (int i = 0; i < 100; i++) {
            publishCreatedMessage(i, TestSubjects.TEST_USER_CREATED_PULL_FROM);
        }

        var seq = 0L;
        do {
            var messages = subscriber.fetch(10, Duration.ofSeconds(3));
            log.debug("got message list size {}", messages.size());
            for (com.jkqj.nats.Message message : messages) {
                log.debug("got msg {} #{}", message.asString(), message.sequence());
                count++;
                message.ack(AckType.AckAck);
                seq = message.sequence();
            }
            subscriber = client.subscribeFrom(TestSubjects.TEST_USER_CREATED_PULL_FROM, seq);

        } while (times++ <= 11);

        assertEquals(100, count);
    }

    @Test
    @SneakyThrows
    public void pullFromLBTest() {
        for (int i = 0; i < 100; i++) {
            publishCreatedMessage(i, TestSubjects.TEST_USER_CREATED_PULL_LB);
        }
        final AtomicInteger count = new AtomicInteger();

        var thread = new Thread(() -> {
            var subscriber = client.subscribe(TestSubjects.TEST_USER_CREATED_PULL_LB);
            var times = 0;

            var seq = 0L;
            do {
                var messages = subscriber.fetch(10, Duration.ofSeconds(3));
                log.debug("[A] got message list size {}", messages.size());
                if (messages.isEmpty()) {
                    break;
                }
                for (com.jkqj.nats.Message message : messages) {
                    log.debug("[A] got msg {} #{}", message.asString(), message.sequence());
                    count.getAndIncrement();
                    message.ack(AckType.AckAck);
                    seq = message.sequence();
                }
                subscriber = client.subscribeFrom(TestSubjects.TEST_USER_CREATED_PULL_LB, seq);

            } while (times++ <= 6);

        });
        thread.start();

        var thread1 = new Thread(() -> {
            var subscriber = client.subscribe(TestSubjects.TEST_USER_CREATED_PULL_LB);
            var times = 0;

            var seq = 0L;
            do {
                var messages = subscriber.fetch(10, Duration.ofSeconds(3));
                log.debug("[B] got message list size {}", messages.size());
                if (messages.isEmpty()) {
                    break;
                }
                for (com.jkqj.nats.Message message : messages) {
                    log.debug("[B] got msg {} #{}", message.asString(), message.sequence());
                    count.getAndIncrement();
                    message.ack(AckType.AckAck);
                    seq = message.sequence();
                }
                subscriber = client.subscribeFrom(TestSubjects.TEST_USER_CREATED_PULL_LB, seq);

            } while (times++ <= 6);

        });
        thread1.start();

        thread.join();
        thread1.join();

        assertEquals(100, count.get());
    }

    @SneakyThrows
    @Test
    public void delayTest() {
        AtomicInteger createdCount = new AtomicInteger();
        AtomicLong seq = new AtomicLong(0);
        CountDownLatch latch = new CountDownLatch(1);

        client.subscribe(TestSubjects.TEST_USER_CREATED_DELAY, e -> {
            var data = e.asString();

            log.debug("got event #{} {}", e.sequence(), data);

            if (seq.compareAndSet(0, e.sequence())) {
                log.debug("nak 10s");
                e.nak(Duration.ofSeconds(3));
            } else {
                log.debug("got delay message");
                createdCount.getAndIncrement();
                e.ack();
                latch.countDown();
            }
        });


        for (var i = 0; i < 1; i++) {
            publishCreatedMessage(i, TestSubjects.TEST_USER_CREATED_DELAY);
        }

        client.flush(Duration.ofSeconds(3));
        latch.await();
        Thread.sleep(2000);
        assertEquals(1, createdCount.get());
    }

    @SneakyThrows
    @Test
    public void scheduleTest() {
        AtomicInteger createdCount = new AtomicInteger();
        CountDownLatch latch = new CountDownLatch(1);

        client.subscribe(TestSubjects.TEST_USER_CREATED_DELAY, e -> {
            var data = e.asString();

            log.debug("got event #{} {}", e.sequence(), data);
            log.debug("got delay message");
            createdCount.getAndIncrement();
            latch.countDown();
        });

        client.publish(TestSubjects.TEST_USER_CREATED_DELAY, ("ping").getBytes(StandardCharsets.UTF_8), Duration.ofSeconds(5));
        client.flush(Duration.ofSeconds(3));
        latch.await();
        assertEquals(1, createdCount.get());
    }

    @SneakyThrows
    @Test
    public void scheduleAtTest() {
        AtomicInteger createdCount = new AtomicInteger();
        CountDownLatch latch = new CountDownLatch(1);

        client.subscribe(TestSubjects.TEST_USER_CREATED_DELAY, e -> {
            var data = e.asString();

            log.debug("got event #{} {}", e.sequence(), data);
            log.debug("got delay message");
            createdCount.getAndIncrement();
            latch.countDown();
        });

        client.publish(TestSubjects.TEST_USER_CREATED_DELAY, Map.of("tt", "vv"), ("ping").getBytes(StandardCharsets.UTF_8), Duration.ofSeconds(5));
        client.flush(Duration.ofSeconds(3));
        latch.await();
        assertEquals(1, createdCount.get());
    }


    public void publishUpdatedMessages(int i, TestSubjects subject) {
        client.publish(subject,
                Map.of("trace-id", NUID.nextGlobal()),
                ("biubiu " + i).getBytes(StandardCharsets.UTF_8));

        client.flush(Duration.ofSeconds(1));
    }

    private void publishCreatedMessage(int i, TestSubjects subject) {
        client.publish(subject, ("ping " + i).getBytes(StandardCharsets.UTF_8));
        client.flush(Duration.ofSeconds(1));
    }


}
