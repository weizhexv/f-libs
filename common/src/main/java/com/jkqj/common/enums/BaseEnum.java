package com.jkqj.common.enums;

/**
 * 枚举基类
 *
 * @author cb
 */
public interface BaseEnum {

    /**
     * 获取枚举中定义的值
     *
     * @return 枚举代码
     */
    Integer getCode();

    /**
     * 获取枚举中定义的备注信息
     *
     * @return 枚举代码描述
     */
    String getDesc();

}