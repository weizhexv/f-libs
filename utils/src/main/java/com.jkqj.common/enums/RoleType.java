package com.jkqj.common.enums;

public enum RoleType {
    ASSISTANT("招聘助手"),
    WHISTLEBLOWER("举报人"),
    PUBLIC_UPLOADER("公共题库上传人");

    private final String desc;

    RoleType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
