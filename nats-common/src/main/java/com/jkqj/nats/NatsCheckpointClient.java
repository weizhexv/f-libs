package com.jkqj.nats;

import com.jkqj.nats.dal.mapper.NatsCheckpointMapper;
import com.jkqj.nats.dal.po.NatsCheckpoint;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Objects.nonNull;

/**
 * @author xuweizhe@reta-inc.com
 * @date 2022/3/21
 * @description
 */
public class NatsCheckpointClient {
    private final String appId;

    @Autowired
    private NatsCheckpointMapper natsCheckpointMapper;

    public NatsCheckpointClient(NatsProperties natsProperties) {
        var checkpointProperties = natsProperties.getCheckpoint();
        checkState(nonNull(checkpointProperties));
        var appId = checkpointProperties.getAppId();
        checkState(StringUtils.isNotBlank(appId));
        this.appId = appId;
    }

    @Transactional
    public boolean refreshSafeDatetime(Subject subject) {
        Subject.check(subject);

        var subjectId = subject.getId();

        return natsCheckpointMapper.refreshSafeDatetime(subjectId, appId) == 1;
    }

    @Transactional(readOnly = true)
    public Optional<LocalDateTime> getSafeDatetime(Subject subject) {
        Subject.check(subject);

        var subjectId = subject.getId();
        var natsCheckpoint = natsCheckpointMapper.find(subjectId, appId);

        return natsCheckpoint.map(NatsCheckpoint::getSafeDatetime);
    }

    @Transactional(readOnly = true)
    public Optional<LocalDateTime> getSafeDatetimeOffset(Subject subject, Duration offset) {
        var safeDatetime = getSafeDatetime(subject);

        return safeDatetime.map(sdt -> sdt.minus(offset));
    }

    @Transactional
    public boolean refreshSequence(Subject subject, Long lastSequence) {
        Subject.check(subject);
        checkArgument(nonNull(lastSequence));

        var subjectId = subject.getId();

        return natsCheckpointMapper.refreshSequence(subjectId, appId, lastSequence) == 1;
    }

    @Transactional(readOnly = true)
    public Optional<Long> getLastSequence(Subject subject) {
        Subject.check(subject);

        var subjectId = subject.getId();
        var natsCheckpoint = natsCheckpointMapper.find(subjectId, appId);

        return natsCheckpoint.map(NatsCheckpoint::getLastSequence);
    }

    @Transactional(readOnly = true)
    public Optional<NatsCheckpoint> getCheckpoint(Subject subject) {
        Subject.check(subject);

        var subjectId = subject.getId();

        return natsCheckpointMapper.find(subjectId, appId);
    }

    @Transactional
    public boolean insertCheckpoint(Subject subject) {
        Subject.check(subject);

        var subjectId = subject.getId();
        var natsCheckpoint = NatsCheckpoint.of(appId, subjectId);

        return natsCheckpointMapper.insert(natsCheckpoint) == 1;
    }

    @Transactional
    public NatsCheckpoint getAndInsertIfAbsent(Subject subject) {
        Subject.check(subject);

        var subjectId = subject.getId();
        var natsCheckpoint = natsCheckpointMapper.find(subjectId, appId);
        if (natsCheckpoint.isPresent()) {
            return natsCheckpoint.get();
        }

        var newNatsCheckpoint = NatsCheckpoint.of(appId, subjectId);
        checkState(natsCheckpointMapper.insert(newNatsCheckpoint) == 1);

        natsCheckpoint = natsCheckpointMapper.find(subjectId, appId);
        checkState(natsCheckpoint.isPresent());

        return natsCheckpoint.get();
    }
}
