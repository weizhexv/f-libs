package com.jkqj.dtrace.monitor.biz;

/**
 * 业务监控打点
 */
public interface BizMetric {
    String BIZ_INDICATOR = "jkqj.biz";

    void monitorCounter(String keyIdentification, int code);

    void monitorTimer(String keyIdentification, int code, long costMillis);
}
