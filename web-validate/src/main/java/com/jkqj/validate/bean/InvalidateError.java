package com.jkqj.validate.bean;

/**
 * @author lingchangmeng
 * @version 1.0.0
 * @ClassName InvalidateError.java
 * @Description 异常信息
 * @createTime 2022/01/11 11:55:00
 */
public class InvalidateError {

    /**
     * 异常参数名称,eg:企业视频id
     */
    private String fieldName;

    /**
     * 异常信息，eg:请求参数[企业视频id]类型不匹配
     */
    private String message;

    public InvalidateError() {
    }

    public InvalidateError(String fieldName, String message) {
        this.fieldName = fieldName;
        this.message = message;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
