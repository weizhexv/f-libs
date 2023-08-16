package com.jkqj.common.enums;

import java.util.Arrays;

public enum BizType {
    Candidate((byte) 1),
    Recruiter((byte) (1 << 1)),
    Headhunter((byte) (1 << 2)),
    Administrator((byte) (1 << 3));

    public byte code;

    BizType(byte code) {
        this.code = code;
    }

    public static boolean hasType(byte types, BizType bizType) {
        return (types & bizType.code) == bizType.code;
    }

    public static byte setType(byte types, BizType bizType) {
        return (byte) (types | bizType.code);
    }

    public static byte unsetType(byte types, BizType bizType) {
        return (byte) (types ^ bizType.code);
    }

    public static byte union(BizType... bizTypes) {
        if (bizTypes == null) {
            return 0;
        }

        return Arrays.stream(bizTypes)
                .map(bizType -> bizType.code)
                .reduce((acc, code) -> (byte) (acc | code))
                .orElse((byte) 0);
    }

    public byte getCode() {
        return code;
    }

    /**
     * 根据code获取BizType
     */
    public static BizType getValueByCode(byte code) {
        for (BizType bizType : BizType.values()) {
            if (bizType.getCode() != code) {
                continue;
            }
            return bizType;
        }
        return null;
    }
}
