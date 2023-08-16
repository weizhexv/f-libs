package com.jkqj.nats;

import io.nats.client.api.KeyValueEntry;

import java.nio.ByteBuffer;
import java.util.Optional;

public class NatsValue implements Value {
    private final ByteBuffer buffer;
    private final long version;

    public NatsValue(KeyValueEntry entry) {
        if (entry != null && entry.getValue() != null) {
            this.buffer = ByteBuffer.wrap(entry.getValue());
            this.buffer.rewind();
            this.version = entry.getRevision();
        } else {
            this.buffer = null;
            this.version = -1;
        }
    }

    @Override
    public Optional<String> getString() {
        if (this.buffer == null) {
            return Optional.empty();
        }
        return Optional.of(new String(this.buffer.array()));
    }

    @Override
    public Optional<Boolean> getBoolean() {
        if (this.buffer == null) {
            return Optional.empty();
        }
        return Optional.of(this.buffer.get() == 1);
    }

    @Override
    public Optional<Byte> getByte() {
        if (this.buffer == null) {
            return Optional.empty();
        }
        return Optional.of(this.buffer.get());
    }

    @Override
    public Optional<Short> getShort() {
        if (this.buffer == null) {
            return Optional.empty();
        }
        return Optional.of(this.buffer.getShort());
    }

    @Override
    public Optional<Integer> getInt() {
        if (this.buffer == null) {
            return Optional.empty();
        }
        return Optional.of(this.buffer.getInt());
    }

    @Override
    public Optional<Long> getLong() {
        if (this.buffer == null) {
            return Optional.empty();
        }

        if (this.buffer.capacity() < Long.BYTES) {
            return Optional.of((long) this.buffer.getInt());
        }

        return Optional.of(this.buffer.getLong());
    }

    @Override
    public Optional<Float> getFloat() {
        if (this.buffer == null) {
            return Optional.empty();
        }
        return Optional.of(this.buffer.getFloat());
    }

    @Override
    public Optional<Double> getDouble() {
        if (this.buffer == null) {
            return Optional.empty();
        }
        return Optional.of(this.buffer.getDouble());
    }

    @Override
    public byte[] getBytes() {
        if (this.buffer == null) {
            return null;
        }
        return this.buffer.array();
    }

    @Override
    public long getVersion() {
        return version;
    }
}
