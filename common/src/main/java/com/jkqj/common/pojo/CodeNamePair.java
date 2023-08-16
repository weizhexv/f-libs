package com.jkqj.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 编码和名称对
 *
 * @author cb
 * @date 2021-12-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeNamePair implements Serializable {

    private String code;

    private String name;

}
