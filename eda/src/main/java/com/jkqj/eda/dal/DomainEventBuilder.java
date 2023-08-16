package com.jkqj.eda.dal;

import com.jkqj.eda.dal.po.DomainEvent;

public class DomainEventBuilder {
    private String appName;
    private String category;
    private String name;
    private String header;
    private String data;
    private String traceId;

    public DomainEventBuilder appName(String appName) {
        this.appName = appName;
        return this;
    }

    public DomainEventBuilder category(String category) {
        this.category = category;
        return this;
    }

    public DomainEventBuilder name(String name) {
        this.name = name;
        return this;
    }

    public DomainEventBuilder header(String header) {
        this.header = header;
        return this;
    }

    public DomainEventBuilder data(String data) {
        this.data = data;
        return this;
    }

    public DomainEventBuilder traceId(String traceId) {
        this.traceId = traceId;
        return this;
    }

    public DomainEvent build() {
        var domainEvent = DomainEvent.init(new DomainEvent());

        domainEvent.setAppName(this.appName);
        domainEvent.setCategory(this.category);
        domainEvent.setName(this.name);
        domainEvent.setHeader(this.header);
        domainEvent.setData(this.data);
        domainEvent.setTraceId(this.traceId);

        DomainEvent.check(domainEvent);
        return domainEvent;
    }
}
