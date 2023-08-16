package com.jkqj.common.enums;

import java.util.Arrays;

/**
 * @author xuweizhe@reta-inc.com
 * @date 2022/1/11
 * @description
 */
public enum AuditTypeEnum {
    RISK,
    QUALITY,
    CAPABILITY,
    ;

    public static AuditTypeEnum of(String name) {
        return Arrays.stream(AuditTypeEnum.values())
                .filter(e -> e.name().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public static AuditTypeEnum first() {
        return RISK;
    }
}
