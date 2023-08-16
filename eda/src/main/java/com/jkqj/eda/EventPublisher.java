package com.jkqj.eda;

import com.jkqj.common.utils.JsonUtils;
import com.jkqj.eda.dal.po.DomainEvent;
import com.jkqj.nats.MessageClient;
import com.jkqj.nats.Subject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

@Slf4j
public class EventPublisher {

    private final MessageClient messageClient;

    public EventPublisher(MessageClient messageClient) {
        checkArgument(messageClient != null);

        this.messageClient = messageClient;
    }

    public void publish(DomainEvent domainEvent) {
        log.info("[EventPublisher.publish] domainEvent {}", JsonUtils.toJson(domainEvent));

        checkArgument(domainEvent != null);
        checkArgument(StringUtils.isNotBlank(domainEvent.getCategory()));
        checkArgument(StringUtils.isNotBlank(domainEvent.getName()));

        Subject subject = new Subject() {
            @Override
            public String getCategory() {
                return domainEvent.getCategory();
            }

            @Override
            public String getName() {
                return domainEvent.getName();
            }
        };

        Map<String, String> headers = null;

        if (StringUtils.isNotBlank(domainEvent.getHeader())) {
            headers = JsonUtils.toMap(domainEvent.getHeader(), String.class, String.class);
        }

        messageClient.publish(subject, headers, domainEvent.getData().getBytes(StandardCharsets.UTF_8));

        log.info("[EventPublisher.publish] publish success uuid {}", domainEvent.getUuid());
    }
}
