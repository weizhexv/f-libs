package com.jkqj.eda.dal.mapper;

import com.jkqj.eda.dal.po.DomainEvent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Mapper
public interface DomainEventMapper {
    int insert(DomainEvent domainEvent);

    int updateById(DomainEvent domainEvent);

    Optional<DomainEvent> findById(@Param("id") Long id);

    List<DomainEvent> find(@Param("appName") String appName, @Param("startAt") LocalDateTime startAt);

    int incRetryCount(@Param("id") Long id);
}
