package com.jkqj.dtrace.monitor;

import com.jkqj.dtrace.annotions.LogLevel;
import com.jkqj.dtrace.context.ReqRunContext;
import com.jkqj.dtrace.output.MonitorOutputHelper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.MDC;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.UUID;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Aspect
@Slf4j
@Setter
public class WebMonitorAspect extends MonitorOutputContext implements ApplicationContextAware {
    private static final String SIDE = "HTTP-SERV";
    private ApplicationContext applicationContext;

//    @Value("${dtrace.level.default:3}")
//    private int defaultLogLevel;
//
//    private LogLevelProvider logLevelProvider;
//
//    private LogOutput logOutput;

    private final String[] whiteArray;
    private final String[] blackArray;

    public WebMonitorAspect(String white, String black) {
        if (!StringUtils.isEmpty(white)) {
            this.whiteArray = StringUtils.split(white, ",");
        } else {
            this.whiteArray = new String[0];
        }
        if (StringUtils.isEmpty(black)) {
            black = "org.springframework.";
        } else {
            black = "org.springframework.," + black;
        }
        this.blackArray = StringUtils.split(black, ",");
    }


//    @PostConstruct
//    public void init() {
//        logLevelProvider = new DefaultLogLevelProvider(defaultLogLevel);
//        logOutput = new DefaultLogOutput(log);
//    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public void getMappingAnnotationPointCut() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void postMappingAnnotationPointCut() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void oldMappingAnnotationPointCut() {
    }

    private boolean needMonitor(String className) {
        for (String w : whiteArray) {
            if (className.startsWith(w)) {
                return true;
            }
        }
        for (String b : blackArray) {
            if (className.startsWith(b)) {
                return false;
            }
        }
        return true;
    }

    @Around("getMappingAnnotationPointCut() || postMappingAnnotationPointCut() || oldMappingAnnotationPointCut()")
    public Object doMonitor(ProceedingJoinPoint pjp) throws Throwable {
        Signature signature = ((MethodInvocationProceedingJoinPoint) pjp).getSignature();
        Class<?> clazz = signature.getDeclaringType();
        if (!needMonitor(clazz.getName())) {
            return pjp.proceed();
        }
        Method method = ((MethodSignature) signature).getMethod();

        String fullPath = clazz.getName() + "." + method.getName();

        ensureLogLevelProvider();

        LogLevel logLevel = this.ensureLogLevelProvider().provide(clazz, method);
        Object retVal = null;
        Throwable e = null;
        long startTime = System.currentTimeMillis();

        ensureLogout();
        try {
            acceptTraceId();
            MonitorOutputHelper.logStart(ensureLogout(), logLevel, fullPath, pjp.getArgs());
            retVal = pjp.proceed();
            return retVal;
        } catch (Throwable throwable) {
            e = throwable;
            throw throwable;
        } finally {
            MonitorOutputHelper.logEnd(ensureLogout(), logLevel, fullPath, retVal, System.currentTimeMillis() - startTime, e);
            clearTraceId();
        }
    }

    private void clearTraceId() {
        MDC.remove(ReqRunContext.TRACE_ID_KEY);
        MDC.remove(ReqRunContext.UID_KEY);
        ReqRunContext.clear();
    }


    private void acceptTraceId() {
        String traceId = (String) RequestContextHolder.currentRequestAttributes().getAttribute(ReqRunContext.TRACE_ID_KEY, RequestAttributes.SCOPE_REQUEST);
        if (StringUtils.isEmpty(traceId)) {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            HttpServletRequest servletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
            traceId = servletRequest.getHeader(ReqRunContext.TRACE_ID_KEY);
            if (StringUtils.isEmpty(traceId)) {
                traceId = UUID.randomUUID().toString() + "-CR";
            }
        }

        MDC.put(ReqRunContext.TRACE_ID_KEY, traceId);
        MDC.put(ReqRunContext.UID_KEY, ReqRunContext.getUserId() == null ? "null" : ReqRunContext.getUserId().toString());
        ReqRunContext.put(ReqRunContext.TRACE_ID_KEY, traceId);
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