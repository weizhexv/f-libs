package com.jkqj.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum RoleEnum {

    BOSS_ROLE(1L),
    HR_ROLE(2L),
    INTERVIEWER_ROLE(3L),
    ;

    private Long code;

    public static RoleEnum getByCode(long code) {
        for (RoleEnum codeEnum : RoleEnum.values()) {
            if (codeEnum.getCode().equals(code)) {
                return codeEnum;
            }
        }
        return null;
    }
}
