package com.jkqj.dtrace.monitor.biz;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class BizMetricStd implements BizMetric {
    private final String appName;
    private final MeterRegistry meterRegistry;

    private static final String APP_NAME = "appName";
    private static final String KI_NAME = "keyId";
    private static final String KI_CODE = "code";

    private long decayOverTime = 60L;

    public BizMetricStd(MeterRegistry meterRegistry, String appName) {
        this.appName = appName;
        this.meterRegistry = meterRegistry;
    }

    @Override
    public void monitorCounter(String keyIdentification, int code) {
        Counter.builder(BIZ_INDICATOR)
                .tag(APP_NAME, appName)
                .tag(KI_NAME, keyIdentification)
                .tag(KI_CODE, code + "")
                .register(meterRegistry)
                .increment();
    }

    @Override
    public void monitorTimer(String keyIdentification, int code, long costMillis) {
        Timer.builder(BIZ_INDICATOR)
                .tag(APP_NAME, appName)
                .tag(KI_NAME, keyIdentification)
                .tag(KI_CODE, code + "")
                .publishPercentileHistogram(false)
                .distributionStatisticExpiry(Duration.ofSeconds(decayOverTime))
                .register(meterRegistry)
                .record(costMillis, TimeUnit.MILLISECONDS);
    }
}
