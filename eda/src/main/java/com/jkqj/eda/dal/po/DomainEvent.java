package com.jkqj.eda.dal.po;

import com.jkqj.eda.EventStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Data
public class DomainEvent {
    private Long id;
    private String appName;
    private String category;
    private String name;
    private String header;
    private String data;
    private String uuid;
    private String traceId;
    private Integer retryCount;
    private Integer published;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Long vsn;

    public static DomainEvent init(DomainEvent domainEvent) {
        domainEvent.setUuid(UUID.randomUUID().toString());
        domainEvent.setRetryCount(0);
        domainEvent.setPublished(EventStatus.UNPUBLISHED.getCode());
        domainEvent.setCreatedAt(LocalDateTime.now());
        domainEvent.setModifiedAt(LocalDateTime.now());
        domainEvent.setVsn(0L);

        return domainEvent;
    }

    public static void check(DomainEvent event) {
        checkArgument(event != null);
        checkArgument(isNotBlank(event.getAppName()));
        checkArgument(isNotBlank(event.getCategory()));
        checkArgument(isNotBlank(event.getName()));
        checkArgument(isNotBlank(event.getData()));
    }

}
