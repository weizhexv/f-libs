package com.jkqj.common.exception;

import com.jkqj.common.enums.BaseEnum;
import lombok.Getter;

/**
 * 自定义业务异常
 *
 * @author cb
 * @date 2020-10-19
 */
@Getter
public class BusinessException extends RuntimeException {
    private Integer code;

    public BusinessException(String message) {
        super(message);
        this.code = 1;
    }

    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(BaseEnum baseEnum) {
        this(baseEnum.getCode(), baseEnum.getDesc());
    }

}