package com.jkqj.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 学历类型
 *
 * @author cb
 */
@Getter
@AllArgsConstructor
public enum EducationType {

    RECRUITMENT("统招"),
    NOT_RECRUITMENT("非统招");

    private String desc;

}
