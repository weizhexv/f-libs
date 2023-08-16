package com.jkqj.job.client.dto;

import com.jkqj.common.collection.CodeNamePairs;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 直面DTO
 *
 * @author cb
 * @date 2022-07-12
 */
@Getter
@Setter
@ToString
public class DirectInterviewDTO implements Serializable {

    private Boolean enabled;

    private LocalDateTime enabledAt;

    private CodeNamePairs categories;

    private Integer experienceFrom;
    private Integer experienceTo;

    private Integer educationFrom;
    private String educationType;

    private Integer fromAge;
    private Integer toAge;

}
