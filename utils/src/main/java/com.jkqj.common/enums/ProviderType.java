package com.jkqj.common.enums;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;

public enum ProviderType {
    Mobile(1),
    DingTalk(2),
    WeChat(3),
    Email(4),
    WeChatMp(5),
    Apple(6),
    Feishu(7),
    SsoDingTalk(8);

    public int code;

    ProviderType(int code) {
        this.code = code;
    }

    /**
     * 根据code获取ProviderType
     */
    public static ProviderType getValueByCode(int code) {
        for (ProviderType providerType : ProviderType.values()) {
            if (providerType.getCode() != code) {
                continue;
            }
            return providerType;
        }
        return null;
    }

    public int getCode() {
        return code;
    }

    public static Optional<ProviderType> of(String name) {
        return Arrays.stream(ProviderType.values())
                .filter(type -> StringUtils.equalsIgnoreCase(name, type.name()))
                .findFirst();
    }
}
