package com.jkqj.dtrace.feign;

import com.jkqj.dtrace.annotions.LogLevel;
import com.jkqj.dtrace.output.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Deprecated
@Slf4j
public class FeignProxy {
    private FeignProxy() {
    }

    public static <T> T proxy(Class<T> tClass, T target, String host, int defaultLogLevel) {
        final LogLevelProvider logLevelProvider = new DefaultLogLevelProvider(defaultLogLevel);
        final LogOutput logOutput = new DefaultLogOutput(LoggerFactory.getLogger(tClass));
        return proxy(tClass, target, host, logLevelProvider, logOutput);
    }

    public static <T> T proxy(Class<T> tClass, T target, String host, LogLevelProvider logLevelProvider, LogOutput logOutput) {
        return (T) Proxy.newProxyInstance(tClass.getClassLoader(), new Class[]{tClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getName().equals("equals") || method.getName().equals("toString")) {
                    return method.invoke(target, args);
                }
                LogLevel logLevel = logLevelProvider.provide(tClass, method);
                String fullPath = tClass.getName() + "." + method.getName() + "/" + host;
                return exec(target, fullPath, method, args, logLevel, logOutput);
            }
        });
    }

    private static Object exec(Object target, String fullPath, Method method, Object[] args, LogLevel logLevel, LogOutput logOutput) throws Throwable {
        Object ret = null;
        Throwable e = null;
        long startTime = System.currentTimeMillis();
        try {
            MonitorOutputHelper.logStart(logOutput, logLevel, fullPath, args);
            ret = method.invoke(target, args);
            return ret;
        } catch (Throwable throwable) {
            log.info("call {} error.", fullPath, throwable);
            e = throwable;
            if (throwable instanceof InvocationTargetException) {
                InvocationTargetException invocationTargetException = (InvocationTargetException) throwable;
                e = invocationTargetException.getTargetException();
                throw invocationTargetException.getTargetException();
            }
            throw throwable;
        } finally {
            MonitorOutputHelper.logEnd(logOutput, logLevel, fullPath, ret, System.currentTimeMillis() - startTime,e);
        }
    }
}
