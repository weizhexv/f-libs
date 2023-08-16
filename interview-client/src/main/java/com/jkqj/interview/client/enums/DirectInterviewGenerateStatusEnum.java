package com.jkqj.interview.client.enums;

import com.jkqj.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 直面生成状态枚举
 *
 * @author liuyang
 */
@Getter
@AllArgsConstructor
public enum DirectInterviewGenerateStatusEnum implements BaseEnum {

    SUCCESS(0, "生成直面成功"),
    UNSATISFIED_DIRECT_INTERVIEW(1, "抱歉，您不满足该职位直面要求"),
    EXIST_INTERVIEW(2, "该职位您已经存在进行中或已完成面试");

    private Integer code;
    private String desc;

}
