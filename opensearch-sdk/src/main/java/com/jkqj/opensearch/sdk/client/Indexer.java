package com.jkqj.opensearch.sdk.client;

import java.util.List;

public interface Indexer {
    <T> boolean index(T data, String tableName, IdTaker<T> idTaker, boolean snakeStyle);
    <T> boolean bulk(List<T> data, String tableName, IdTaker<T> idTaker, boolean snakeStyle);
    default <T> boolean index(T data, String tableName, IdTaker<T> idTaker) {
        return index(data, tableName, idTaker, true);
    }
    default <T> boolean bulk(List<T> data, String tableName, IdTaker<T> idTaker){
        return bulk(data,tableName,idTaker,true);
    }
}
