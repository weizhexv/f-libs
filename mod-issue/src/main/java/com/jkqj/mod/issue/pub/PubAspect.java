package com.jkqj.mod.issue.pub;

import com.jkqj.common.utils.JsonUtils;
import com.jkqj.mod.issue.msg.IssueMessage;
import com.jkqj.mod.issue.pub.output.MsgDbOutput;
import com.jkqj.mod.issue.pub.output.MsgOutput;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.ibatis.annotations.Param;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Slf4j
public class PubAspect implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    private final Map<String, MsgDbOutput> cache = new ConcurrentHashMap<>();

    private final MsgOutput mqMsgOutput;

    public PubAspect(MsgOutput mqMsgOutput) {
        this.mqMsgOutput = mqMsgOutput;
    }

    @Pointcut("execution(* com.jkqj..dal.mapper.*.*(..))")
    public void pubPoint() {
    }

    @Around("pubPoint()")
    public Object aroundInvoke(ProceedingJoinPoint joinPoint) throws Throwable {
        // 1. 目标注解获取
        Context context = MapperExtractor.getCachedContext(joinPoint.getTarget());
        Object result = joinPoint.proceed();
        // 2. 无注解 或 查询类方法 直接返回
        if (context == Context.NONE || !context.isChange(joinPoint.getSignature().getName())) {
            return result;
        }
        // 3. 发送消息
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Parameter[] parameters = method.getParameters();
        doIssue(context, joinPoint.getArgs(), parameters);
        return result;
    }

    private void doIssue(Context context, Object[] args, Parameter[] parameters) {
        // 1. 组装消息
        Object idValue = getIdValue(context, args, parameters);
        IssueMessage message = new IssueMessage();
        message.setValue(idValue);
        String simpleName = context.pub.poClass().getSimpleName();
        String source = simpleName.endsWith("PO") ? simpleName.substring(0, simpleName.lastIndexOf("PO")) : simpleName;
        message.setSource(source);
        String json = JsonUtils.toJson(message);
        log.info("开始发送增量，消息内容为：{}", json);
        // 2. 发送消息
        try {
            mqMsgOutput.output(json);
        } catch (RuntimeException e) {
            log.error("output msg to mq error:{},then save to db", json, e);
            String beanName = context.pub.msgDbOutputBeanName();
            MsgDbOutput msgDbOutput = cache.get(beanName);
            if (msgDbOutput == null) {
                msgDbOutput = applicationContext.getBean(context.pub.msgDbOutputBeanName(), MsgDbOutput.class);
                cache.putIfAbsent(beanName, msgDbOutput);
            }
            msgDbOutput.output(json);
        }
    }

    private Object getIdValue(Context context, Object[] args, Parameter[] parameters) {
        String idName = context.pub.id();
        int index = 0;
        for (Parameter p : parameters) {
            Param param = p.getAnnotation(Param.class);
            if (param == null) {
                index++;
                continue;
            }
            if (param.value().equals(idName)) {
                return args[index];
            }
            index++;
        }

        for (Object arg : args) {
            if (arg != null && context.pub.poClass().isAssignableFrom(arg.getClass())) {
                try {
                    return FieldUtils.readField(arg, idName, true);
                } catch (IllegalAccessException e) {
                    log.error("get id error", e);
                    throw new RuntimeException(e);
                }
            }
        }
        throw new RuntimeException("不能抽取id值");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
