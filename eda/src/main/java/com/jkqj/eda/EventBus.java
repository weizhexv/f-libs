package com.jkqj.eda;

import com.jkqj.common.utils.JsonUtils;
import com.jkqj.eda.dal.mapper.DomainEventMapper;
import com.jkqj.eda.dal.po.DomainEvent;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.SchedulerLock;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public class EventBus {
    private static final Duration OFFSET = Duration.ofHours(2);

    private final EdaConfig.EdaProperties properties;
    private final EventPublisher eventPublisher;
    private final EventStore eventStore;
    private final SqlSessionFactory sqlSessionFactory;

    public EventBus(EdaConfig.EdaProperties properties, EventPublisher publisher, EventStore store, SqlSessionFactory factory) {
        this.properties = properties;
        this.eventPublisher = publisher;
        this.eventStore = store;
        this.sqlSessionFactory = factory;
    }

    public boolean save(DomainEvent domainEvent) {
        log.info("[EventBus.save] domain event {}", JsonUtils.toJson(domainEvent));
        return eventStore.save(domainEvent);
    }

    @Scheduled(fixedDelay = 5000)
    @SchedulerLock(name = "EventBus.scheduledPublish", lockAtMostFor = 5000)
    public void scheduledPublish() {
        String appName = properties.getAppName();
        LocalDateTime startAt = LocalDateTime.now().minus(OFFSET);

        List<DomainEvent> domainEvents = eventStore.findUnpublished(appName, startAt);

        log.info("[EventBus.scheduledPublish] appName {} startAt {} domainEvents {}", appName, startAt, JsonUtils.toJson(domainEvents));

        if (CollectionUtils.isEmpty(domainEvents)) {
            log.info("[EventBus.scheduledPublish] idle, no unpublished events");
            return;
        }

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        for (DomainEvent domainEvent : domainEvents) {

            LifeCycle lifeCycle = null;
            DomainEventMapper mapper = null;
            try {
                lifeCycle = new SessionLifeCycle(sqlSessionFactory);
                lifeCycle.begin();

                mapper = lifeCycle.bind(DomainEventMapper.class);
                domainEvent.setPublished(EventStatus.PUBLISHED.getCode());

                if (mapper.updateById(domainEvent) == 1) {
                    eventPublisher.publish(domainEvent);

                    lifeCycle.commit();
                } else {
                    lifeCycle.rollback();
                }
            } catch (Throwable th) {
                log.error("[EventBus.scheduledPublish] error", th);

                if (lifeCycle != null) {
                    lifeCycle.rollback();
                }

                if (mapper != null) {
                    if (mapper.incRetryCount(domainEvent.getId()) == 1) {
                        log.info("[EventBus.scheduledPublish] incRetryCount, retries {}", domainEvent.getRetryCount() + 1);
                    } else {
                        log.warn("[EventBus.scheduledPublish] incRetryCount failed, domainEvent {}", domainEvent);
                    }
                }

            } finally {
                if (lifeCycle != null) {
                    try {
                        lifeCycle.close();

                    } catch (Throwable th) {
                        log.error("[EventBus.scheduledPublish] session close error", th);
                    }
                }
            }
        }

        stopWatch.stop();
        log.info("[EventBus.scheduledPublish] publish domain events size {} cost {}ms",
                domainEvents.size(), stopWatch.getLastTaskTimeMillis());
    }
}
