package com.jkqj.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 公共错误枚举
 *
 * @author cb
 */
@Getter
@AllArgsConstructor
public enum CommonErrorEnum implements BaseEnum {

    SYSTEM_ERROR(5001, "系统异常"),
    SYSTEM_BUSY(5002, "系统繁忙,请稍候再试"),

    GATEWAY_NOT_FOUND_SERVICE(5003, "服务未找到"),
    GATEWAY_ERROR(5004, "网关异常"),
    GATEWAY_CONNECT_TIME_OUT(5005, "网关超时"),

    ARGUMENT_NOT_VALID(4001, "请求参数校验不通过"),
    INVALID_TOKEN(4002, "无效token"),
    UPLOAD_FILE_SIZE_LIMIT(4003, "上传文件大小超过限制"),

    DUPLICATE_PRIMARY_KEY(4004,"已存在重复记录"),
    LOGIN_ERROR(4005, "登录失败"),
    NOT_LOGIN_IN(4006, "用户未登录"),
    USER_NOT_EXISTS(4007, "用户不存在"),
    OPERATION_FAILED(4008, "操作失败"),
    NO_PERMISSION(4009, "无操作权限"),
    ILLEGAL_OPERATION(4010, "非法操作"),
    METHOD_PARAM_BIND_FAILED(4011, "参数绑定异常"),
    EXCEED_ACCESS_TIMES(4012, "超出访问次数限制"),
    EXISTS_REFERENCE_DATA(4013, "存在关联引用数据"),
    RECORD_NOT_EXISTS(4014, "记录不存在");

    /**
     * 错误类型码
     */
    private Integer code;

    /**
     * 错误类型描述信息
     */
    private String desc;

}
