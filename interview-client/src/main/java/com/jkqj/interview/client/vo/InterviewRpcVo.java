package com.jkqj.interview.client.vo;

import com.jkqj.common.collection.LongIdNamePairs;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 面试Vo
 *
 * @author liuyang
 */
@Getter
@Setter
@ToString
public class InterviewRpcVo implements Serializable {

    /**
     * 面试ID
     */
    private long interviewId;

    /**
     * 面试状态
     */
    private int status;

    /**
     * 面试状态显示名称
     */
    private String statusName;

    /**
     * 创建时间
     */
    private LocalDateTime createAt;


    /**
     * 截止时间
     */
    private LocalDateTime expireAt;

    /**
     * 截止时间
     */
    private long expireTimeStamp;

    /**
     * 开始时间
     */
    private long startTimeStamp;

    /**
     * 面试类型
     */
    private int interviewType;

    /**
     * 提交时间
     */
    private LocalDateTime commitAt;

    /**
     * 修改时间
     */
    private LocalDateTime modifiedAt;

    /**
     * 职位
     */
    private Job job;

    /**
     * 面试官
     */
    private Interviewer propositioner;

    /**
     * 是否驳回重答
     */
    private boolean reject;

    /**
     * 问题版本，用于端强制用户升级app版本，来自问题的client version信息
     */
    private int maxQuestionVersion;

    /**
     * 预计答题时长
     */
    private int duration;

    /**
     * 直面标识
     */
    private boolean directInterview;

    /**
     * 定时面试信息
     */
    private ScheduleInterview scheduleInterview;

    @Getter
    @Setter
    @ToString
    public static class Job implements Serializable {


        /**
         * 职位ID
         */
        private long jobId;
        /**
         * 职位ID
         */
        private long jobSnapshotId;

        /**
         * 职位名称
         */
        private String title;

        /**
         * 职位描述
         */
        private String description;

        /**
         * 公司id
         */
        private Long companyId;
        /**
         * 公司名称
         */
        private String companyName;

        /**
         * 公司图标
         */
        private String companyLogo;

        /**
         * 公司规模
         */
        private String companyScale;

        /**
         * 融资阶段
         */
        private String companyFinStage;

        /**
         * 部门名称
         */
        private String orgName;

        /**
         * 所属行业类型
         */
        private String industryName;

        /**
         * 关键词
         */
        private LongIdNamePairs keywords;

        /**
         * 薪资
         */
        private String salaryDesc;

        /**
         * 经验
         */
        private String experienceDesc;

        /**
         * 地址
         */
        private String addressDesc;

        /**
         * 最低教育经历
         */
        private String educationFrom;

        /**
         * 发布人
         */
        private PublishBy publishBy;

    }

    @Getter
    @Setter
    @ToString
    public static class PublishBy implements Serializable {

        /**
         * 发布人名称
         */
        private String name;

        /**
         * 发布人头像
         */
        private String avatar;

        /**
         * 发布人职位名称
         */
        private String title;

        /**
         * 发布人用户id
         */
        private Long userId;
    }


    @Getter
    @Setter
    @ToString
    public static class Interviewer implements Serializable {

        /**
         * 面试官名称
         */
        private String name;

        /**
         * 面试官头像
         */
        private String avatar;

        /**
         * 面试官职位名称
         */
        private String title;

    }


    @Getter
    @Setter
    @ToString
    public static class ScheduleInterview implements Serializable {
        private int status;
        private String statusName;
    }

}
