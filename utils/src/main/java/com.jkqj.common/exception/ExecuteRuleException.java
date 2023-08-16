package com.jkqj.common.exception;

import java.util.Map;

/**
 * 执行规则异常
 *
 * @author cb
 */
public class ExecuteRuleException extends Exception {

    /**
     * 执行规则异常构造
     *
     * @param params 参数
     * @param rule   规则
     * @param cause  异常栈
     */
    public ExecuteRuleException(Map<String, Object> params, String rule, Throwable cause) {
        super("execute rule:" + rule + " error of params: " + params, cause);
    }
}