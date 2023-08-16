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
public class CandidateRpcVo implements Serializable{

    /**
     * 候选人数目
     */
    private int candidateCount;

    /**
     * 已提交面试数据
     */
    private int submitInterviewCount;

}
