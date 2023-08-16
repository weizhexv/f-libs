package com.jkqj.common.enums;

import com.jkqj.common.utils.MyEnumUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author xuweizhe@reta-inc.com
 * @date 2022/1/11
 * @description
 */
@Getter
@AllArgsConstructor
public enum AuditStatusEnum {
    WAITING(0, "待审核"),
    PASS(1, "通过"),
    REJECT(2, "驳回"),
    FAIL(3, "不通过"),
    ;

    private final int code;
    private final String desc;

    public static int init() {
        return WAITING.code;
    }

    public static boolean isLegal(Integer code) {
        if (Objects.isNull(code)) {
            return false;
        }
        List<Integer> codes
                = Arrays.stream(AuditStatusEnum.values()).map(AuditStatusEnum::getCode).collect(Collectors.toList());

        return codes.contains(code);
    }

    public static String getDesc(int code) {
        return Optional.ofNullable(MyEnumUtils.codeOf(AuditStatusEnum.class, code))
                .map(AuditStatusEnum::getDesc).orElse("未知");
    }
}
