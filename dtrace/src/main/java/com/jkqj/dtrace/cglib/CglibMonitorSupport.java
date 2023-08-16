package com.jkqj.dtrace.cglib;

import com.google.common.base.Stopwatch;
import com.jkqj.dtrace.annotions.LogLevel;
import com.jkqj.dtrace.monitor.MonitorOutputContext;
import com.jkqj.dtrace.output.MonitorOutputHelper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Slf4j
public class CglibMonitorSupport extends MonitorOutputContext implements ApplicationContextAware, MethodInterceptor {
    private static final String SIDE = "CGLIB";
    private ApplicationContext applicationContext;

    public <T> T proxy(Class<T> cls) {
        return (T) Enhancer.create(cls, this);
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        if (method.getName().equals("equals") || method.getName().equals("toString")) {
            return methodProxy.invokeSuper(obj, args);
        }

        ensureLogLevelProvider();
        LogLevel logLevel = ensureLogLevelProvider().provide(obj.getClass(), method);
        String fullPath = obj.getClass().getName() + "." + method.getName();

        Object result = null;
        Throwable e = null;
        ensureLogout();
        Stopwatch stopwatch = Stopwatch.createStarted();

        try {
            MonitorOutputHelper.logStart(ensureLogout(), logLevel, fullPath, args);
            result = methodProxy.invokeSuper(obj, args);

            return result;
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
            MonitorOutputHelper.logEnd(ensureLogout(), logLevel, fullPath, result, stopwatch.elapsed(TimeUnit.MILLISECONDS), e);
        }
    }

    @Override
    protected ApplicationContext getSpringApplicationContext() {
        return applicationContext;
    }

    @Override
    protected Logger getCurrentLogger() {
        return log;
    }

    @Override
    protected String side() {
        return SIDE;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
