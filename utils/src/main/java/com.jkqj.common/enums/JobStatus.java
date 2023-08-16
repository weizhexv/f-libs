package com.jkqj.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum JobStatus {
    DRAFT(0, "草稿"),
    TO_PUBLISH(1, "待发布"),
    AUDITING(2, "审核中"),
    AUDIT_REJECT(3, "审核失败"),
    PUBLISHED(4, "招聘中"),
    CLOSED(5, "已关闭"),
    ;

    private Integer code;
    private String desc;
}
