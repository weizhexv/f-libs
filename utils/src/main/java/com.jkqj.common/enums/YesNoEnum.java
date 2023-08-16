package com.jkqj.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 是否枚举
 *
 * @author cb
 */
@Getter
@AllArgsConstructor
public enum YesNoEnum implements BaseEnum {

    NO(0, "否"),
    YES(1, "是");

    private Integer code;

    private String desc;

    public static boolean isYes(Integer code) {
        return YES.code.equals(code);
    }

    public static Integer getCode(boolean result) {
        return result ? YES.code : NO.code;
    }

}
