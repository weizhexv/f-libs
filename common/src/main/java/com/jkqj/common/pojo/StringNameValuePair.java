package com.jkqj.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 字符串名称和值对
 *
 * @author cb
 * @date 2021-12-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StringNameValuePair implements Serializable {

    private String name;

    private String value;

}
