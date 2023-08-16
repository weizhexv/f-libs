package com.jkqj.dtrace.output;

import com.jkqj.dtrace.annotions.LogLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class MonitorOutputHelper {
    private static final boolean isNotWebApp;

    public static final String WEB_APP_VAR_NAME = "current.web_app";

    static {
        String varValue = System.getProperty(WEB_APP_VAR_NAME);
        isNotWebApp = "false".equals(varValue);
    }

    private MonitorOutputHelper() {
    }

    public static void logStart(LogOutput logOutput, LogLevel logLevel, String fullPath, Object[] args) {
        try {
            if (!logLevel.hasParam()) {
                logOutput.outputStartWithoutParam(fullPath, ignoreSomeWebArg(args), new Object[]{"不展示"});
                return;
            }
            logOutput.outputStart(fullPath, ignoreSomeWebArg(args));
        } catch (RuntimeException e) {
            log.info("logStart出现问题，但不影响业务流程", e);
        }
    }


    public static void logEnd(LogOutput logOutput, LogLevel logLevel, String fullPath, Object value, long cost, Throwable throwable) {
        try {
            if (!logLevel.hasReturn()) {
                logOutput.outputEndWithoutReturnValue(fullPath, cost, value, "不展示", throwable);
                return;
            }
            logOutput.outputEnd(fullPath, cost, value, throwable);
        } catch (RuntimeException e) {
            log.info("logEnd出现问题，但不影响业务流程", e);
        }
    }


    private static Object[] ignoreSomeWebArg(Object[] args) {
        if (isNotWebApp || args == null || args.length == 0) {
            return args;
        }
        Object[] newArgs = new Object[args.length];

        boolean needNew = false;
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg instanceof MultipartFile) {
                newArgs[i] = "upload file:" + ((MultipartFile) arg).getOriginalFilename();
                needNew = true;
                continue;
            }
            if (arg instanceof HttpServletResponse) {
                newArgs[i] = "HttpServletResponse parameter";
                needNew = true;
                continue;
            }
            if (arg instanceof HttpServletRequest) {
                newArgs[i] = "HttpServletRequest parameter";
                needNew = true;
                continue;
            }
            if (arg instanceof ModelAndView) {
                newArgs[i] = "ModelAndView parameter";
                needNew = true;
                continue;
            }
            newArgs[i] = arg;
        }
        return needNew ? newArgs : args;
    }
}
