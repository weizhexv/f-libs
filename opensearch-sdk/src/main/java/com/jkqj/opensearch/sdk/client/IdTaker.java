package com.jkqj.opensearch.sdk.client;

public interface IdTaker<T> {
    Long taker(T data);
}
