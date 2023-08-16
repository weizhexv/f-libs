package com.jkqj.dtrace.monitor;

import com.jkqj.dtrace.annotions.LogLevel;
import com.jkqj.dtrace.helper.MetricHelper;
import com.jkqj.dtrace.output.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

public abstract class MonitorOutputContext {
    private volatile LogLevelProvider logLevelProvider;

    private volatile LogOutput logOutput;


    protected abstract ApplicationContext getSpringApplicationContext();

    protected abstract Logger getCurrentLogger();

    protected abstract String side();

    protected final LogLevelProvider ensureLogLevelProvider() {
        if (logLevelProvider != null) {
            return logLevelProvider;
        }
        LogLevelProvider local = null;
        try {
            local = getSpringApplicationContext().getBean("logLevelProvider", LogLevelProvider.class);
        } catch (BeansException beansException) {
            getCurrentLogger().info("不用担心,不影响业务流程: can't find logLevelProvider bean.");
        }
        if (local == null) {
            local = new DefaultLogLevelProvider(getDefaultLogLevel());
        }
        logLevelProvider = local;
        return logLevelProvider;
    }

    protected final LogOutput ensureLogout() {
        if (logOutput != null) {
            return logOutput;
        }
        logOutput = composite();
        return logOutput;
    }

    private LogOutput composite() {
        LogOutput local = null;

        try {
            local = getSpringApplicationContext().getBean(LogOutput.UNIFIED_BEAN_NAME, LogOutput.class);
        } catch (BeansException beansException) {
            getCurrentLogger().info("不用担心,不影响业务流程: can't find logOutput bean.");
        }
        if (null == local) {
            local = new DefaultLogOutput(getCurrentLogger());
        }
        MetricOutput metricOutput = null;
        try {
            metricOutput = getSpringApplicationContext().getBean(MetricOutput.UNIFIED_BEAN_NAME, MetricOutput.class);
        } catch (BeansException beansException) {
            getCurrentLogger().info("不用担心,不影响业务流程: can't find metricOutput bean.");
        }
        if (null == local) {
            local = new DefaultLogOutput(getCurrentLogger());
        }
        if (metricOutput == null) {
            return local;
        }
        return build(local, metricOutput);
    }

    private LogOutput build(final LogOutput local, final MetricOutput metricOutput) {
        return new LogOutput() {
            @Override
            public void outputStart(String fullPath, Object[] args) {
                local.outputStart(fullPath, args);
            }

            @Override
            public void outputEnd(String fullPath, long cost, Object value, Throwable throwable) {
                local.outputEnd(fullPath, cost, value, throwable);
                metricOutput.metric(MetricHelper.buildStdTags(fullPath, value, side()), cost, throwable != null);
            }

            @Override
            public void outputEndWithoutReturnValue(String fullPath, long cost, Object value, String fakeReturn, Throwable throwable) {
                local.outputEnd(fullPath, cost, fakeReturn, throwable);

                metricOutput.metric(MetricHelper.buildStdTags(fullPath, value, side()), cost, throwable != null);
            }
        };
    }


    private int getDefaultLogLevel() {
        String defLog = getSpringApplicationContext().getEnvironment().getProperty("dtrace.level.default");
        if (StringUtils.isEmpty(defLog)) {
            return LogLevel.ALL.getValue();
        }
        return Integer.parseInt(defLog);
    }
}
