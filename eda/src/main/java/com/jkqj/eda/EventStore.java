package com.jkqj.eda;

import com.jkqj.eda.dal.mapper.DomainEventMapper;
import com.jkqj.eda.dal.po.DomainEvent;

import java.time.LocalDateTime;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class EventStore {
    private final DomainEventMapper mapper;

    public EventStore(DomainEventMapper mapper) {
        this.mapper = mapper;
    }

    public List<DomainEvent> findUnpublished(String appName, LocalDateTime startAt) {
        checkArgument(isNotBlank(appName));
        checkArgument(startAt != null);

        return mapper.find(appName, startAt);
    }

    public boolean save(DomainEvent domainEvent) {
        DomainEvent.check(domainEvent);

        return mapper.insert(domainEvent) == 1;
    }
}
