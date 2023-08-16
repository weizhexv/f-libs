package com.jkqj.dtrace.output;

import com.jkqj.dtrace.annotions.LogLevel;

import java.lang.reflect.Method;

public interface LogLevelProvider {
    LogLevel provide(Class clazz, Method method);
}
