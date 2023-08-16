package com.jkqj.eda;


import com.jkqj.eda.dal.mapper.DomainEventMapper;
import com.jkqj.nats.MessageClient;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

import static com.google.common.base.Preconditions.checkState;

@Configuration
@EnableConfigurationProperties(EdaConfig.EdaProperties.class)
public class EdaConfig {

    @Autowired
    private EdaProperties edaProperties;

    @Data
    @ConfigurationProperties(prefix = "jkqj.eda")
    public static class EdaProperties {
        private String appName;
    }

    @PostConstruct
    public void init() {
        checkState(edaProperties != null);
        checkState(StringUtils.isNotBlank(edaProperties.getAppName()));
    }

    @Bean
    public EventStore eventStore(@Autowired DomainEventMapper mapper) {
        return new EventStore(mapper);
    }

    @Bean
    public EventPublisher eventPublisher(@Autowired MessageClient messageClient) {
        return new EventPublisher(messageClient);
    }

    @Bean
    public EventBus eventBus(@Autowired EdaProperties properties, @Autowired EventPublisher publisher,
                             @Autowired EventStore store, @Autowired SqlSessionFactory factory) {
        return new EventBus(properties, publisher, store, factory);
    }

    @Bean
    public EdaClient edaClient(@Autowired EventBus eventBus) {
        return new EdaClient(eventBus);
    }
}
