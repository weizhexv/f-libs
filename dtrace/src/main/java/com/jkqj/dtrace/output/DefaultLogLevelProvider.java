package com.jkqj.dtrace.output;

import com.jkqj.dtrace.annotions.LogLevel;
import com.jkqj.dtrace.annotions.MonitorConf;

import java.lang.reflect.Method;

public class DefaultLogLevelProvider implements LogLevelProvider{
    private final int defaultLogLevel;

    public DefaultLogLevelProvider(int defaultLogLevel) {
        this.defaultLogLevel = defaultLogLevel;
    }

    @Override
    public LogLevel provide(Class clazz, Method method) {
        MonitorConf monitorConf = method.getAnnotation(MonitorConf.class);
        if (monitorConf != null) {
            return monitorConf.logLevel();
        }
        monitorConf = (MonitorConf) clazz.getAnnotation(MonitorConf.class);
        if (monitorConf != null) {
            return monitorConf.logLevel();
        }
        LogLevel defaultLL = LogLevel.ofIntValue(defaultLogLevel);
        if (defaultLL == null) {
            throw new RuntimeException("invalid log level value :" + defaultLogLevel);
        }
        return defaultLL;
    }
}
