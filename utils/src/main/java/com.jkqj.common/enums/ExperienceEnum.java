package com.jkqj.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

/**
 * 经验枚举
 *
 * @author cb
 */
@Getter
@AllArgsConstructor
public enum ExperienceEnum implements BaseEnum {

    UNLIMITED(-1, "经验不限"),
    ZERO(0, "无经验"),
    ONE(1, "1年"),
    TOW(2, "2年"),
    THREE(3, "3年"),
    FOUR(4, "4年"),
    FIVE(5, "5年"),
    SIX(6, "6年"),
    SEVEN(7, "7年"),
    EIGHT(8, "8年"),
    NINE(9, "9年"),
    TEN(10, "10年"),
    TEN_PLUS(100, "10年以上");

    private Integer code;
    private String desc;

    public static Pair<Integer, Integer> getExperienceRange(String fromDesc, String toDesc) {
        Integer from;
        Integer to;

        if ("经验不限".equals(fromDesc) || "无经验要求".equals(fromDesc)) {
            return Pair.of(0, 100);
        } else if ("无经验".equals(fromDesc)) {
            from = 0;
        } else if ("10年以上".equals(fromDesc)) {
            return Pair.of(10, 100);
        } else {
            from = Integer.valueOf(fromDesc.replaceAll("年", ""));
        }

        if (StringUtils.isBlank(toDesc) || "经验不限".equals(toDesc) || "无经验要求".equals(toDesc) || "10年以上".equals(toDesc)) {
            to = 100;
        } else {
            to = Integer.valueOf(toDesc.replaceAll("年", ""));
        }

        return Pair.of(from, to);
    }

}
