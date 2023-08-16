package com.jkqj.job.client.dto;

import com.jkqj.common.pojo.BUser;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 职位详情DTO
 *
 * @author cb
 * @date 2022/5/9
 */
@Getter
@Setter
@ToString
public class JobDetailDTO implements Serializable {

    private Long jobId;

    private String title;
    private String description;

    private String salaryDesc;

    private Integer headcount;

    private String experienceDesc;

    private String educationFrom;

    private String officeLocation;

    private DirectInterviewDTO directInterview;

    private BUser publishedBy;

    private CompanyDTO company;

}
