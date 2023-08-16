package com.jkqj.interview.client.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 线索状态
 *
 * @author liuyang
 */
@Getter
@Setter
@ToString
public class CLeadsStatusRpcVo implements Serializable {

    /**
     * 是否上传简历
     */
    private boolean uploadCv;

    /**
     * 线索状态
     */
    private Integer leadsStatus;

    /**
     * 简历名称
     */
    private String cvName;

    /**
     * 简历id
     */
    private Long cvSnapshotId;


    public CLeadsStatusRpcVo() {
    }

    public CLeadsStatusRpcVo(boolean uploadCv, Integer leadsStatus, String cvName, Long cvSnapshotId) {
        this.uploadCv = uploadCv;
        this.leadsStatus = leadsStatus;
        this.cvName = cvName;
        this.cvSnapshotId = cvSnapshotId;
    }

}
