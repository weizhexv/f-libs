package com.jkqj.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 字符串编码和值对
 *
 * @author cb
 * @date 2022-09-30
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StringCodeValuePair implements Serializable {

    private String code;

    private String value;

}
