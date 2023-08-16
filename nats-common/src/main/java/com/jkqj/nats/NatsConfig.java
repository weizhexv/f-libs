package com.jkqj.nats;

import io.nats.client.JetStreamApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;
import java.io.IOException;

@Slf4j
@Configuration
@EnableConfigurationProperties(NatsProperties.class)
public class NatsConfig {
    @Autowired
    private ConnectionCache connCache;

    @Bean
    public ConnectionCache connectionCache(@Autowired NatsProperties properties) {
        return new ConnectionCache(properties.getPoolSize(), new ConnectionAllocator(properties));
    }

    @Bean
    public MessageClient messageClient(@Autowired NatsProperties properties) {
        return new JetStreamMessageClient(properties, connectionCache(properties));
    }

    @Bean
    public KvClient kvClient(@Autowired NatsProperties properties) {
        return new JetStreamKvClient(connectionCache(properties));
    }

    @Bean
    @Conditional(NatsCheckpointCondition.class)
    public NatsCheckpointClient natsCheckpointClient(@Autowired NatsProperties properties) {
        return new NatsCheckpointClient(properties);
    }

    @PreDestroy
    public void destroy() {
        if (connCache != null) {
            connCache.shutdown();
        }
    }
}
