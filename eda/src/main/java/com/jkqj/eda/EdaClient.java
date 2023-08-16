package com.jkqj.eda;

import com.jkqj.eda.dal.po.DomainEvent;

public class EdaClient {
    private final EventBus eventBus;

    public EdaClient(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public boolean publish(DomainEvent domainEvent) {
        DomainEvent.check(domainEvent);

        return eventBus.save(domainEvent);
    }
}
