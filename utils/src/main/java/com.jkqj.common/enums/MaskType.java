package com.jkqj.common.enums;

public enum MaskType {
    DELETED((byte) 1),
    VISIBLE((byte) 2);

    public byte value;

    MaskType(byte value) {
        this.value = value;
    }

    public static boolean has(byte masks, MaskType maskType) {
        return (masks & maskType.value) == maskType.value;
    }
}
