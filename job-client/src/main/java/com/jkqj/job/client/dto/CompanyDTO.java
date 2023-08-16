package com.jkqj.job.client.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 公司DTO
 *
 * @author cb
 * @date 2021/12/14
 */
@Data
public class CompanyDTO implements Serializable {

    /**
     * 公司id
     */
    private Long id;

    /**
     * 公司全称
     */
    private String name;

    /**
     * 公司简称
     */
    private String shortName;

    /**
     * 公司性质
     */
    private String kind;

    /**
     * 公司Logo
     */
    private String logoUrl;

    /**
     * 公司简介
     */
    private String description;

    /**
     * 联系人
     */
    private String contacts;

    /**
     * 营业执照照片
     */
    private String licenseUrl;

    /**
     * 公司行业名称
     */
    private String industryName;

    /**
     * 公司规模
     */
    private String scale;

    /**
     * 融资阶段
     */
    private String finStage;

}
