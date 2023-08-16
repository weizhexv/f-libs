package com.jkqj.nats;

import io.nats.client.JetStreamApiException;
import io.nats.client.KeyValue;
import io.nats.client.api.KeyValueConfiguration;
import io.nats.client.api.StorageType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkArgument;

@Slf4j
public class JetStreamKvClient implements KvClient {
    private final ConnectionCache connCache;

    public JetStreamKvClient(ConnectionCache connCache) {
        this.connCache = connCache;
    }

    @Override
    public synchronized Bucket addBucket(String name) {
        return addBucket(name, StorageType.Memory);
    }

    private Bucket addBucket(String name, StorageType storageType) {
        checkArgument(StringUtils.isNotBlank(name));

        try {
            var connection = connCache.conn();
            var kvm = connection.keyValueManagement();
            if (!kvm.getBucketNames().contains(name)) {
                var config = KeyValueConfiguration.builder()
                        .storageType(storageType)
                        .name(name)
                        .build();

                kvm.create(config);
            }
            var keyValue = connection.keyValue(name);
            return new NatsBucket(keyValue);
        } catch (IOException | JetStreamApiException | InterruptedException e) {
            log.error("can't add bucket {}", name, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Bucket addBucket(String name, boolean persistent) {
        return addBucket(name, persistent ? StorageType.File : StorageType.Memory);
    }

    @Override
    public void delBucket(String name) {
        checkArgument(StringUtils.isNotBlank(name));

        try {
            connCache.conn().keyValueManagement().delete(name);
        } catch (IOException | JetStreamApiException e) {
            log.error("delete bucket {} error", name, e);
        }
    }

    @Override
    public void delBucket(KeyValue kv) {
        delBucket(kv.getBucketName());
    }
}
