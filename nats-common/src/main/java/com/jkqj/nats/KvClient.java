package com.jkqj.nats;

import io.nats.client.KeyValue;

public interface KvClient {
    Bucket addBucket(String name);

    Bucket addBucket(String name, boolean persistent);

    void delBucket(String name);

    void delBucket(KeyValue kv);
}
