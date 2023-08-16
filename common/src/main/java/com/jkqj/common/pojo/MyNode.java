package com.jkqj.common.pojo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 节点
 *
 * @author cb
 * @date 2022-07-20
 */
@Getter
@Setter
@ToString
public class MyNode implements Serializable {

    private Object id;
    private String text;
    private List<MyNode> children;

}
