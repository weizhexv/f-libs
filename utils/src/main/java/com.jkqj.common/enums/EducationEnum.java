package com.jkqj.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 学历枚举
 *
 * @author cb
 */
@Getter
@AllArgsConstructor
public enum EducationEnum implements BaseEnum {

    UNLIMITED(0, "不限", 0),
    JUNIOR_OR_BELOW(1, "初中及以下", 1),
    TECHNICAL_SECONDARY(2, "中专/中技", 2),
    SENIOR(3, "高中", 2),
    JUNIOR_COLLEGE(4, "大专", 3),
    REGULAR_COLLEGE(5, "本科", 4),
    MASTER(6, "硕士", 5),
    DOCTOR(7, "博士", 6);

    private Integer code;
    private String desc;
    private Integer level;

    public static EducationEnum getEnumByDesc(String desc) {
        for (EducationEnum educationEnum : EducationEnum.values()) {
            if (educationEnum.getDesc().equals(desc)) {
                return educationEnum;
            }
        }
        return null;
    }

    public static EducationEnum getEnumByCode(Integer code) {
        for (EducationEnum educationEnum : EducationEnum.values()) {
            if (educationEnum.getCode().equals(code)) {
                return educationEnum;
            }
        }
        return null;
    }

    public static String getLowOneLevelDesc(String desc) {
        if (UNLIMITED.getDesc().equals(desc)) {
            return UNLIMITED.getDesc();
        }

        EducationEnum educationEnum = getEnumByDesc(desc);
        if (educationEnum == null) {
            return UNLIMITED.getDesc();
        }

        EducationEnum lowOneLevelEducation = getEnumByCode(educationEnum.getCode() - 1);

        return lowOneLevelEducation.getDesc();
    }


}
