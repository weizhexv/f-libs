package com.jkqj.nats;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.ByteBufferInput;
import com.esotericsoftware.kryo.kryo5.io.ByteBufferOutput;
import io.nats.client.JetStreamApiException;
import io.nats.client.KeyValue;
import io.nats.client.api.KeyValueEntry;
import io.nats.client.api.KeyValueWatchOption;
import io.nats.client.api.KeyValueWatcher;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
public class NatsBucket implements Bucket {
    private static final Kryo kryo;

    static {
        kryo = new Kryo();
        kryo.setRegistrationRequired(false);

        try {
            kryo.register(Class.forName("java.util.ImmutableCollections$List12"));
            kryo.register(Class.forName("java.util.ImmutableCollections$ListN"));
            kryo.register(Class.forName("java.util.ImmutableCollections$SubList"));
            kryo.register(Class.forName("java.util.ImmutableCollections$Map1"));
            kryo.register(Class.forName("java.util.ImmutableCollections$MapN"));
            kryo.register(Class.forName("java.util.ImmutableCollections$Set12"));
            kryo.register(Class.forName("java.util.ImmutableCollections$SetN"));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        kryo.register(HashMap.class);
        kryo.register(LocalTime.class);
        kryo.register(LocalDate.class);
        kryo.register(LocalDateTime.class);
    }

    private final KeyValue kv;

    public NatsBucket(KeyValue kv) {
        this.kv = kv;
    }

    @Override
    public String getName() {
        return this.kv.getBucketName();
    }

    @Override
    public Optional<String> getString(String key) {
        return get(key).getString();
    }

    @Override
    public Optional<Integer> getInt(String key) {
        return get(key).getInt();
    }

    @Override
    public Optional<Long> getLong(String key) {
        return get(key).getLong();
    }

    @Override
    public Optional<Float> getFloat(String key) {
        return get(key).getFloat();
    }

    @Override
    public Optional<Double> getDouble(String key) {
        return get(key).getDouble();
    }

    @Override
    public Optional<LocalTime> getTime(String key) {
        var nanoOfDay = get(key).getLong();
        if (nanoOfDay.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(LocalTime.ofNanoOfDay(nanoOfDay.get()));
    }

    @Override
    public Optional<LocalDateTime> getDateTime(String key) {
        var milliSeconds = get(key).getLong();
        if (milliSeconds.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(LocalDateTime.ofInstant(Instant.ofEpochMilli(milliSeconds.get()), ZoneId.systemDefault()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> Optional<Map<String, V>> getMap(String key) {
        try (var input = new ByteBufferInput(get(key).getBytes())) {
            var map = kryo.readClassAndObject(input);
            return Optional.ofNullable((Map<String, V>) map);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Optional<T> getObject(String key, Class<T> type) {
        Value value = get(key);
        if (ArrayUtils.isEmpty(value.getBytes())) {
            return Optional.empty();
        }

        try (var input = new ByteBufferInput(value.getBytes())) {
            var object = kryo.readClassAndObject(input);
            return Optional.ofNullable((T) object);
        }
    }

    @Override
    public byte[] getBytes(String key) {
        try {
            return this.kv.get(key).getValue();
        } catch (IOException | JetStreamApiException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Optional<Boolean> getBoolean(String key) {
        return get(key).getBoolean();
    }

    @Override
    public Optional<Byte> getByte(String key) {
        return get(key).getByte();
    }

    @Override
    public Optional<Short> getShort(String key) {
        return get(key).getShort();
    }

    @Override
    public boolean put(String key, String value) {
        return put(key, value.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public boolean put(String key, byte[] value) {
        try {
            this.kv.put(key, value);
            return true;
        } catch (IOException | JetStreamApiException e) {
            log.warn("put {} error", key, e);
            return false;
        }
    }

    @Override
    public boolean put(String key, boolean value) {
        return put(key, (byte) (value ? 1 : 0));
    }

    @Override
    public boolean put(String key, byte value) {
        return put(key, new byte[]{value});
    }

    @Override
    public boolean put(String key, short value) {
        return put(key, ByteBuffer.allocate(Short.BYTES).putShort(value).array());
    }

    @Override
    public boolean put(String key, int value) {
        return put(key, ByteBuffer.allocate(Integer.BYTES).putInt(value).array());
    }

    @Override
    public boolean put(String key, long value) {
        return put(key, ByteBuffer.allocate(Long.BYTES).putLong(value).array());
    }

    @Override
    public boolean put(String key, float value) {
        return put(key, ByteBuffer.allocate(Float.BYTES).putFloat(value).array());
    }

    @Override
    public boolean put(String key, double value) {
        return put(key, ByteBuffer.allocate(Double.BYTES).putDouble(value).array());
    }

    @Override
    public boolean put(String key, LocalTime value) {
        return put(key, value.toNanoOfDay());
    }

    @Override
    public boolean put(String key, LocalDateTime value) {
        return put(key, value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }

    @Override
    public <V> boolean putMap(String key, Map<String, V> value) {
        return put(key, valueToBytes(value));
    }

    @Override
    public boolean putObject(String key, Object value) {
        return put(key, valueToBytes(value));
    }

    private byte[] valueToBytes(Object value) {
        try (var output = new ByteBufferOutput(256, 2048)) {
            kryo.writeClassAndObject(output, value);
            return output.toBytes();
        }

    }

    @Override
    public boolean delete(String key) {
        try {
            this.kv.purge(key);
        } catch (IOException | JetStreamApiException e) {
            return false;
        }
        return true;
    }

    @Override
    public Value get(String key) {
        try {
            return new NatsValue(this.kv.get(key));
        } catch (IOException | JetStreamApiException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public boolean cas(String key, byte[] value, long version) {
        try {
            this.kv.update(key, value, version);
        } catch (IOException | JetStreamApiException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean watch(String key, Consumer<Value> watcher) {
        try {
            this.kv.watch(key, new KeyValueWatcher() {
                @Override
                public void watch(KeyValueEntry keyValueEntry) {
                    watcher.accept(new NatsValue(keyValueEntry));
                }

                @Override
                public void endOfData() {
                }
            }, KeyValueWatchOption.UPDATES_ONLY);

            return true;
        } catch (IOException | JetStreamApiException | InterruptedException e) {
            log.warn("watch error {}", key, e);
            return false;
        }
    }

}
