package com.jkqj.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SalaryType implements BaseEnum {
    MONTHLY(0, "月"),
    YEARLY(1, "年"),
    HOURLY(2, "小时"),
    DAILY(3, "日");

    private Integer code;
    private String desc;
}
