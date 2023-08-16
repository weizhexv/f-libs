package com.jkqj.common.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Long型ID和名称对
 *
 * @author cb
 * @date 2021-12-27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LongIdNamePair implements Serializable {

    private Long id;

    private String name;

}
