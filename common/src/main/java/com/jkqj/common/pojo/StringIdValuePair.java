package com.jkqj.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 字符串ID和值对
 *
 * @author cb
 * @date 2022-05-05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StringIdValuePair implements Serializable {

    private String id;

    private String value;

}
