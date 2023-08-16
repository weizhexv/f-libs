package com.jkqj.nats;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public interface Bucket {
    String getName();

    Optional<String> getString(String key);

    byte[] getBytes(String key);

    Optional<Boolean> getBoolean(String key);

    Optional<Byte> getByte(String key);

    Optional<Short> getShort(String key);

    Optional<Integer> getInt(String key);

    Optional<Long> getLong(String key);

    Optional<Float> getFloat(String key);

    Optional<Double> getDouble(String key);

    Optional<LocalTime> getTime(String key);

    Optional<LocalDateTime> getDateTime(String key);

    <V> Optional<Map<String, V>> getMap(String key);

    <T> Optional<T> getObject(String key, Class<T> type);

    boolean put(String key, String value);

    boolean put(String key, byte[] value);

    boolean put(String key, boolean value);

    boolean put(String key, byte value);

    boolean put(String key, short value);

    boolean put(String key, int value);

    boolean put(String key, long value);

    boolean put(String key, float value);

    boolean put(String key, double value);

    boolean put(String key, LocalTime value);

    boolean put(String key, LocalDateTime value);

    <V> boolean putMap(String key, Map<String, V> value);

    boolean putObject(String key, Object value);

    boolean delete(String key);

    Value get(String key);

    boolean cas(String key, byte[] value, long version);

    boolean watch(String key, Consumer<Value> watcher);
}
