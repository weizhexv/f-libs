package com.jkqj.common.enums;

/**
 * @author xuweizhe@reta-inc.com
 * @date 2022/1/18
 * @description
 */
public enum BlackWordsType {
    AUDIT_INTERVIEW_RISK("auditInterviewRisk");

    BlackWordsType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    private final String name;
}
