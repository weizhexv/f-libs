package com.jkqj.nats;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.nats.client.*;
import io.nats.client.api.*;
import io.nats.client.impl.Headers;
import io.nats.client.impl.NatsMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import static com.google.common.base.Preconditions.checkArgument;

@Slf4j
public class JetStreamMessageClient implements MessageClient {

    private final ConnectionCache connCache;
    private final Cache<String, StreamInfo> streamCache;
    private final Cache<String, SubjectEntry> subjectCache;
    private final NatsProperties properties;

    public JetStreamMessageClient(NatsProperties properties, ConnectionCache connCache) {
        this.properties = properties;
        this.streamCache = Caffeine.newBuilder()
                .maximumSize(2048)
                .initialCapacity(200)
                .build();
        this.subjectCache = Caffeine.newBuilder()
                .maximumSize(2048)
                .initialCapacity(200)
                .build();

        this.connCache = connCache;
        this.connCache.addEventHandler(this::onConnectionEvents);
    }

    @Override
    public void publish(Subject subject, byte[] body) {
        Subject.check(subject);
        checkArgument(body != null);

        var message = NatsMessage.builder()
                .subject(subject.getName())
                .data(body)
                .build();

        doPublish(subject, message);
    }

    @Override
    public void publish(Subject subject, Map<String, String> headers, byte[] body) {
        Subject.check(subject);
        checkArgument(body != null);

        var messageHeaders = buildHeaders(headers);
        var message = NatsMessage.builder()
                .subject(subject.getName())
                .headers(messageHeaders)
                .data(body)
                .build();

        doPublish(subject, message);
    }

    @Override
    public void publish(Subject subject, byte[] body, Duration delay) {
        checkArgument(delay != null);
        publish(subject, new HashMap<>(), body, delay);
    }

    @Override
    public void publish(Subject subject, Map<String, String> headers, byte[] body, Duration delay) {
        checkArgument(delay != null);

        var delayValue = String.valueOf(delay.toMillis());
        var pubAtValue = String.valueOf(System.currentTimeMillis());
        if (headers == null) {
            headers = Map.of(HEADER_DELAY, delayValue, HEADER_PUB_AT, pubAtValue);
        } else {
            headers = new HashMap<>(headers);
            headers.put(HEADER_DELAY, delayValue);
            headers.put(HEADER_PUB_AT, pubAtValue);
        }

        publish(subject, headers, body);
    }

    @Override
    public void subscribe(Subject subject, Consumer<Message> handler) {
        Subject.check(subject);
        checkArgument(handler != null);

        doSubscribe(subject, ZonedDateTime.now(), handler);
    }

    @Override
    public void subscribeFrom(Subject subject, Duration offset, Consumer<Message> handler) {
        Subject.check(subject);
        checkArgument(offset != null);
        checkArgument(handler != null);

        var from = ZonedDateTime.now().minus(offset);
        doSubscribe(subject, from, handler);
    }

    @Override
    public void subscribeFrom(Subject subject, LocalDateTime startAt, Consumer<Message> handler) {
        Subject.check(subject);
        checkArgument(startAt != null);
        checkArgument(handler != null);

        var from = startAt.atZone(ZoneId.systemDefault());
        doSubscribe(subject, from, handler);
    }

    @Override
    public Subscriber subscribe(Subject subject) {
        Subject.check(subject);

        var jss = doPullSubscribe(subject, null);
        return new JetStreamSubscriber(jss);
    }

    @Override
    public Subscriber subscribeFrom(Subject subject, long sequence) {
        Subject.check(subject);
        checkArgument(sequence > 0);

        var jss = doPullSubscribe(subject, sequence);
        return new JetStreamSubscriber(jss);
    }

    private JetStreamSubscription doPullSubscribe(Subject subject, Long sequence) {
        log.debug("pull subscribing {}, {}", subject, sequence);

        initStream(subject);

        var config = buildPullConsumerConfig(subject, sequence);
        var options = PullSubscribeOptions.builder()
                .stream(subject.getCategory())
                .configuration(config)
                .durable(subject.getId())
                .build();
        try {
            return jetStream().subscribe(subject.getName(), options);
        } catch (IOException | JetStreamApiException e) {
            log.error("can't pull subscribe", e);
            throw new RuntimeException(e);
        }
    }

    private ConsumerConfiguration buildPullConsumerConfig(Subject subject, Long sequence) {
        var configOpt = lookupConsumerConfig(subject.getCategory(), subject.getId());
        ConsumerConfiguration config;
        if (configOpt.isPresent()) {
            config = configOpt.get();
        } else {
            var builder = ConsumerConfiguration.builder()
                    .maxPullWaiting(properties.getMaxPullWaiting());

            if (sequence == null || sequence <= 0) {
                builder.deliverPolicy(DeliverPolicy.New);
            } else {
                builder.deliverPolicy(DeliverPolicy.ByStartSequence).startSequence(sequence);
            }

            config = builder.durable(subject.getId()).build();
        }
        return config;
    }

    private void doSubscribe(Subject subject, ZonedDateTime startTime, Consumer<Message> handler) {
        log.debug("subscribing {}", subject);

        initStream(subject);

        try {
            var queuing = StringUtils.isNotBlank(subject.getGroup().orElse(null));
            var subjectEntry = subjectCache.get(subject.getId(), __ -> new SubjectEntry());
            var dispatcher = subjectEntry.getDispatcher();
            var messageHandler = new JetMessageHandler(handler);
            var options = buildPushSubscribeOptions(subject, queuing, startTime);

            log.debug("subscribe options {}", options);

            if (queuing) {
                jetStream().subscribe(subject.getName(), subject.getGroup().orElseThrow(), dispatcher, messageHandler, false, options);
            } else {
                jetStream().subscribe(subject.getName(), dispatcher, messageHandler, false, options);
            }
        } catch (IOException | JetStreamApiException | ExecutionException e) {
            log.error("subscribe error on {}", subject, e);
            throw new RuntimeException(e);
        }
    }

    private PushSubscribeOptions buildPushSubscribeOptions(Subject subject, boolean queuing, ZonedDateTime startTime) throws ExecutionException {
        var durableName = queuing ? subject.getId() : null;
        var configOpt = lookupConsumerConfig(subject.getCategory(), durableName);
        ConsumerConfiguration config;
        if (configOpt.isPresent()) {
            config = configOpt.get();
        } else {
            var configBuilder = ConsumerConfiguration.builder()
                    .ackPolicy(AckPolicy.Explicit)
                    .durable(durableName);
            if (startTime == null) {
                configBuilder.deliverPolicy(DeliverPolicy.All);
            } else {
                configBuilder.deliverPolicy(DeliverPolicy.ByStartTime)
                        .startTime(startTime);
            }

            config = configBuilder.build();
        }

        return PushSubscribeOptions.builder()
                .stream(subject.getCategory())
                .durable(durableName)
//                .ordered(!queuing)
                .configuration(config)
                .build();
    }

    private Optional<ConsumerConfiguration> lookupConsumerConfig(String stream, String durableName) {
        try {
            var jsm = connCache.jsm();
            var consumerInfo = jsm.getConsumerInfo(stream, durableName);
            consumerInfo.throwOnHasError();
            ConsumerConfiguration config = consumerInfo.getConsumerConfiguration();
            return Optional.ofNullable(config);
        } catch (IOException | JetStreamApiException e) {
            log.debug("can't get consumer", e);
        }

        return Optional.empty();
    }

    @Override
    public void flush(Duration duration) {
        try {
            connCache.conn().flush(duration);
        } catch (TimeoutException | InterruptedException e) {
            log.error("flush error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean unsubscribe(Subject subject) {
        Subject.check(subject);

        try {
            var jsm = connCache.jsm();
            return jsm.deleteConsumer(subject.getCategory(), subject.getId());
        } catch (IOException | JetStreamApiException e) {
            log.debug("can't delete consumer", e);
            return false;
        }
    }

    private void initStream(Subject subject) {
        log.debug("cache stats {}", streamCache.stats());

        if (streamCache.getIfPresent(subject.getId()) != null) {
            log.debug("hit cache {}", subject.getId());
            return;
        }

        var streamInfo = addStream(subject);
        streamCache.put(subject.getId(), streamInfo);
    }

    private StreamInfo addStream(Subject subject) {
        var jsm = connCache.jsm();
        try {
            var info = jsm.getStreamInfo(subject.getCategory());
            if (info != null && !info.hasError()) {
                updateStream(subject.getCategory(), subject.getName());
                return info;
            }
        } catch (IOException | JetStreamApiException e) {
            log.debug("get stream error", e);
        }

        log.debug("adding stream {}", subject);

        try {
            return jsm.addStream(buildStreamConfig(subject));
        } catch (IOException | JetStreamApiException ex) {
            log.error("add stream error {}", subject, ex);
            throw new RuntimeException(ex);
        }
    }

    private void updateStream(String stream, String subject) {
        var jsm = connCache.jsm();
        try {
            var exitedConfig = jsm.getStreamInfo(stream).getConfiguration();
            if (exitedConfig.getSubjects().contains(subject)) {
                return;
            }

            log.debug("updating stream {} for {}", stream, subject);

            exitedConfig.getSubjects().add(subject);
            jsm.updateStream(exitedConfig);
        } catch (IOException | JetStreamApiException e) {
            log.error("update stream error", e);
            throw new RuntimeException(e);
        }
    }

    private StreamConfiguration buildStreamConfig(Subject subject) {
        var builder = StreamConfiguration.builder()
                .name(subject.getCategory())
                .storageType(StorageType.File)
                .subjects(subject.getName());
        if (isClusterMode()) {
            log.debug("building stream with cluster mode [replicas:{}]", properties.getReplicas());
            builder.replicas(properties.getReplicas());
        }

        return builder.build();
    }

    private boolean isClusterMode() {
        var conn = connCache.conn();
        return conn.getServers().size() > 1
                && StringUtils.isNotBlank(conn.getServerInfo().getCluster())
                && StringUtils.isNotBlank(conn.getServerInfo().getServerName());
    }

    private void doPublish(Subject subject, NatsMessage message) {
        initStream(subject);

        try {
            var options = PublishOptions.builder()
                    .stream(subject.getCategory())
                    .messageId(NUID.nextGlobal())
                    .build();

            var ack = jetStream().publish(message, options);
            ack.throwOnHasError();
            if (ack.isDuplicate()) {
                log.info("duplicated message to {}", subject);
            }
        } catch (IOException | JetStreamApiException e) {
            log.error("can't publish message to {}", subject);
            throw new RuntimeException(e);
        }
    }

    private JetStream jetStream() {
        return connCache.js();
    }

    private Headers buildHeaders(Map<String, String> headers) {
        var messageHeaders = new Headers();
        if (headers == null || headers.isEmpty()) {
            return messageHeaders;
        }

        headers.forEach(messageHeaders::add);

        return messageHeaders;
    }

    public void onConnectionEvents(Connection conn, ConnectionListener.Events events) {
        log.debug("on event {}", events.name());
        if (events == ConnectionListener.Events.DISCONNECTED) {
            log.debug("invalidated stream cache");
            streamCache.invalidateAll();
        }
    }

    private class SubjectEntry {
        private Dispatcher dispatcher;

        public Dispatcher getDispatcher() {
            if (dispatcher == null || !dispatcher.isActive()) {
                dispatcher = connCache.conn().createDispatcher();
            }
            return dispatcher;
        }

        public SubjectEntry() {
            this.dispatcher = connCache.conn().createDispatcher();
        }
    }
}
