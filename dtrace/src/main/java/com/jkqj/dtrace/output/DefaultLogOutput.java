package com.jkqj.dtrace.output;

import com.jkqj.common.utils.JsonUtils;
import com.jkqj.dtrace.context.ReqRunContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

@Slf4j
public class DefaultLogOutput implements LogOutput {
    private final Logger logger;

    public DefaultLogOutput() {
        this(null);
    }

    public DefaultLogOutput(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void outputStart(String fullPath, Object[] args) {
        getUsingLogger().info("start--os:{},platform:{},av:{},bt:{},m:{},p:{}", ReqRunContext.getOS(), ReqRunContext.getPlatform(), ReqRunContext.getAppVersion(), ReqRunContext.getUserBizTypes(), fullPath, JsonUtils.toJson(args));
    }

    @Override
    public void outputEnd(String fullPath, long cost, Object value, Throwable throwable) {
        if (throwable == null) {
            getUsingLogger().info("end--cost {}ms, m: {},r:{}", cost, fullPath, JsonUtils.toJson(value));
        } else {
            getUsingLogger().error("end--cost {}ms, m: {}", cost, fullPath, throwable);
        }
    }


    private Logger getUsingLogger() {
        if (logger != null) {
            return logger;
        }
        return log;
    }
}
