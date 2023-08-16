package com.jkqj.common.enums;

/**
 * @author lvwenl
 * <p>
 * 职位创建来源
 */

public enum JobCreatedSource implements IntValuableEnum {
    /**
     * 抢镜
     */
    QJ(1),

    /**
     * 钉钉
     */
    DING_DING(2),

    ;


    private final int code;

    JobCreatedSource(int code) {
        this.code = code;
    }

    @Override
    public int getValue() {
        return code;
    }
}
