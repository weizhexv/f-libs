package com.jkqj.interview.client.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Setter
@Getter
@ToString
public class InterviewProcRpcVO implements Serializable {
    /**
     * 面试 id
     */
    private Long id;
    /**
     * 用户 id
     */
    private long cid;
    /**
     * 职位 id
     */
    private long jobId;
    /**
     * 职位快照 id
     */
    private long jobSnapshotId;
    /**
     * 邀请码
     */
    private String invitationCode;
    /**
     * 现在轮次
     */
    private int curRound;
    /**
     * 面试 id
     */
    private long interviewId;

    /**
     * 简历快照 id
     */
    private Long cvCenterId;

    /**
     * 面试类型
     */
    private int interviewType;
    /**
     * 面试状态(一级状态)
     */
    private int interviewStatus;
    /**
     * 状态附加标记(二级状态)
     */
    private long interviewStatusFlag;
    /**
     * 面试状态
     */
    private int status;

    /**
     * 后续人ID
     */
    private Long candidateId;
}
