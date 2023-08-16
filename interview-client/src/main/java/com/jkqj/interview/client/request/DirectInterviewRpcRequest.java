package com.jkqj.interview.client.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 直面Request
 *
 * @author liuyang
 */
@Getter
@Setter
@ToString
public class DirectInterviewRpcRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 职位id
     */
    private Long jobId;

    /**
     * 职类code
     */
    private List<String> categories;

    /**
     * 工作经验
     */
    private Integer experience;

    /**
     * 教育经历列表
     */
    private List<Education> educationList;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 教育
     */
    @Getter
    @Setter
    @ToString
    public static class Education implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * 是否统招
         */
        private String educationType;

        /**
         * 学位信息
         */
        private String education;
    }

}
