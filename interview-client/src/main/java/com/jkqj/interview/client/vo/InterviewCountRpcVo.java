package com.jkqj.interview.client.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 面试Vo
 *
 * @author liuyang
 */
@Getter
@Setter
@ToString
public class InterviewCountRpcVo implements Serializable{

    /**
     * 职位id
     */
    private long jobId;

    /**
     * 新简历数目
     */
    private int unitLeadsCount;

    /**
     * 已筛选简历数目
     */
    private int passLeadsCount;

    /**
     * 非终态面试数目
     */
    private int otherInterviewCount;

    /**
     * 通过简历数目
     */
    private int passInterviewCount;

    /**
     * 不合适数目
     */
    private int failLeadsAndInterviewCount;

    /**
     * 全部面试数目
     */
    private int allInterviewCount;

    /**
     * 未回答面试数目
     */
    private int unansweredInterviewCount;

    /**
     * 未评估面试数目
     */
    private int needEvaluateInterviewCount;

    /**
     * 已评估面试数目
     */
    private int evaluatedInterviewCount;

}
