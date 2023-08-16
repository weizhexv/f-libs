package com.jkqj.eda;

public interface LifeCycle {
    void begin();

    <T> T bind(Class<T> clazz);

    void commit();

    void rollback();

    void close();
}
