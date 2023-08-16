package com.jkqj.nats;

import java.util.Optional;

public interface Value {
    Optional<String> getString();

    Optional<Boolean> getBoolean();

    Optional<Byte> getByte();

    Optional<Short> getShort();

    Optional<Integer> getInt();

    Optional<Long> getLong();

    Optional<Float> getFloat();

    Optional<Double> getDouble();

    byte[] getBytes();

    long getVersion();
}
