package com.jkqj.dtrace.feign;

import com.jkqj.dtrace.annotions.LogLevel;
import com.jkqj.dtrace.monitor.MonitorOutputContext;
import com.jkqj.dtrace.output.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Slf4j
public class FeignMonitorSupport extends MonitorOutputContext implements ApplicationContextAware {
    public static final String SIDE = "HTTP-CLI";
    private ApplicationContext applicationContext;


    public  <T> T proxy(Class<T> tClass, T target, String host) {
        return (T) Proxy.newProxyInstance(tClass.getClassLoader(), new Class[]{tClass}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.getName().equals("equals") || method.getName().equals("toString")) {
                    return method.invoke(target, args);
                }
                ensureLogLevelProvider();
                LogLevel logLevel = ensureLogLevelProvider().provide(tClass, method);
                String fullPath = tClass.getName() + "." + method.getName() + "#" + host;

                return exec(target, fullPath, method, args, logLevel);
            }
        });
    }

    private Object exec(Object target, String fullPath, Method method, Object[] args, LogLevel logLevel) throws Throwable {
        Object ret = null;
        Throwable e = null;
        long startTime = System.currentTimeMillis();
        ensureLogout();
        try {

            MonitorOutputHelper.logStart(ensureLogout(), logLevel, fullPath, args);
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
            MonitorOutputHelper.logEnd(ensureLogout(), logLevel, fullPath, ret, System.currentTimeMillis() - startTime,e);
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
