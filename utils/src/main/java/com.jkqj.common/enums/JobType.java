package com.jkqj.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JobType implements BaseEnum {
    FULL_TIME(0, "全职"),
    PART_TIME(1, "兼职"),
    INTERN(2, "实习生"),
    CONTRACT(3, "合同工"),
    CASUAL(4, "临时工");

    private Integer code;
    private String desc;
}
