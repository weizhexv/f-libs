package com.jkqj.interview.client.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 直面Vo
 *
 * @author liuyang
 */
@Getter
@Setter
@ToString
public class DirectInterviewRpcVo implements Serializable {

    /**
     * 直面状态 0: 申请成功  1: 直面要求不符合  2: 已存在直面面试
     */
    private Integer status;

    /**
     * 失败原因
     */
    private String failReason;

    /**
     * 失败详情
     */
    private String failDetailReason;

    /**
     * 面试信息
     */
    private InterviewRpcVo interview;

}
