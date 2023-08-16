package com.jkqj.nats.dal.po;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author xuweizhe@reta-inc.com
 * @date 2022/3/21
 * @description
 */
@Getter
@Setter
public class NatsCheckpoint {
    private String appId;
    private String subjectId;
    private LocalDateTime safeDatetime;
    private Long lastSequence;
    private LocalDateTime modifiedAt;
    private LocalDateTime createdAt;

    public static NatsCheckpoint of(String appId, String subjectId) {
        var natsCheckpoint = new NatsCheckpoint();
        natsCheckpoint.setAppId(appId);
        natsCheckpoint.setSubjectId(subjectId);
        natsCheckpoint.setSafeDatetime(LocalDateTime.now());
        natsCheckpoint.setModifiedAt(LocalDateTime.now());
        natsCheckpoint.setCreatedAt(LocalDateTime.now());

        return natsCheckpoint;
    }
}
