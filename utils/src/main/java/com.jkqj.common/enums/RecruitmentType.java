package com.jkqj.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RecruitmentType implements BaseEnum {
    SOCIAL(0, "社招"),
    ON_CAMPUS(1, "校招"),
    PRACTICE(2, "实习");

    private Integer code;
    private String desc;

    public static RecruitmentType getEnumByDesc(String desc) {
        for (RecruitmentType type : RecruitmentType.values()) {
            if (type.getDesc().equals(desc)) {
                return type;
            }
        }
        return null;
    }

}
