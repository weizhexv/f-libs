package com.jkqj.dubbo.global.validate;

import lombok.Getter;

import java.util.List;

/**
 * dubbo参数验证异常
 *
 * @author rolandhe
 *
 */
@Getter
public class DubboValidatedException extends RuntimeException {
    private final List<String> messageList;

    public DubboValidatedException(List<String> messageList) {
        this.messageList = messageList;
    }
}
