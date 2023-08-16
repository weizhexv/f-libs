package com.jkqj.nats.dal.mapper;

import com.jkqj.nats.dal.po.NatsCheckpoint;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

/**
 * @author xuweizhe@reta-inc.com
 * @date 2022/3/21
 * @description
 */
@Mapper
public interface NatsCheckpointMapper {
    int refreshSafeDatetime(@Param("subjectId") String subjectId, @Param("appId") String appId);

    int refreshSequence(@Param("subjectId") String subjectId, @Param("appId") String appId, @Param("lastSequence") Long lastSequence);

    Optional<NatsCheckpoint> find(@Param("subjectId") String subjectId, @Param("appId") String appId);

    int insert(NatsCheckpoint natsCheckpoint);
}
