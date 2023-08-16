package com.jkqj.opensearch.sdk.aspect;

import com.jkqj.opensearch.sdk.utils.OsSearchMysqlStringEscape;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

/**
 * 转义去除
 *
 * @author liuyang
 */
@Aspect
@Slf4j
public class EscapeAspect {

    public EscapeAspect() {
    }

    @Pointcut("execution(* com.jkqj..os.mapper.*.*(..))")
    public void pubPoint() {
    }

    @Before("pubPoint()")
    public void aroundInvoke(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg == null || !(arg instanceof String)) {
                continue;
            }
            if(OsSearchMysqlStringEscape.isEscapeNeededForString((String) arg)){
                throw new RuntimeException("非法注入参数，需转义！");
            }
        }
    }

}
