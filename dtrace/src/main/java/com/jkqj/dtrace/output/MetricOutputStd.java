package com.jkqj.dtrace.output;

import com.jkqj.common.constants.Symbols;
import com.jkqj.dtrace.helper.MetricHelper;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MetricOutputStd implements MetricOutput {
    public static final String CODE = "code";
    public static final String SUCCESS = "success";
    public static final String EXP = "exp";


    private final MeterRegistry meterRegistry;
    private final String appName;

    private String percent = "0.5,0.9,0.99,0.999";
    private long decayOverTime = 60L;

    private double[] percentiles;

    public MetricOutputStd(MeterRegistry meterRegistry, String appName) {
        this.meterRegistry = meterRegistry;
        this.appName = appName;
        this.percentiles = Symbols.SPLITTER.splitToStream(percent).mapToDouble(Double::parseDouble).toArray();
    }

    @Override
    public void metric(MetricStdTags stdTags, long cost, boolean hasExp) {
        // perf -- STD_INDICATOR_PERF
        // tags: appName,url,side,exp,[success,code]
        this.handleTimerMetric(stdTags, cost, hasExp);
        // exp -- STD_INDICATOR_EXP_COUNTER if hasExp is true
        // tags: appName, url, side
        if (hasExp) {
            this.handleCounterMetric(stdTags);
        }
    }

    @Override
    public void counterExp(MetricStdTags stdTags) {
        try {
            List<Tag> counterTags = generateCommonTags(stdTags);
            Counter.builder(STD_INDICATOR_EXP_COUNTER)
                    .tags(counterTags)
                    .register(meterRegistry)
                    .increment();
        }catch (RuntimeException e) {
            log.info("counterExp met exp", e);
        }
    }

    private void handleTimerMetric(MetricStdTags stdTags, long cost, boolean hasExp) {
        List<Tag> tags = generateCommonTags(stdTags);
        tags.add(Tag.of(CODE, stdTags.getCode()==null?"":stdTags.getCode()));
        tags.add(Tag.of(SUCCESS, stdTags.getSuccess() == null?"":stdTags.getSuccess()));
        tags.add(Tag.of(EXP, hasExp ? "1" : "0"));

        Timer.Builder builder = Timer.builder(STD_INDICATOR_PERF)
                .tags(tags)
                .publishPercentileHistogram(false)
                .distributionStatisticExpiry(Duration.ofSeconds(getDecayOverTime()));
        if (percentiles != null && percentiles.length > 0) {
            builder.publishPercentiles(percentiles);
        }
        builder.register(meterRegistry).record(cost, TimeUnit.MILLISECONDS);
    }

    private void handleCounterMetric(MetricStdTags stdTags) {
        List<Tag> counterTags = generateCommonTags(stdTags);
        Counter.builder(STD_INDICATOR_EXP_COUNTER)
                .tags(counterTags)
                .register(meterRegistry)
                .increment();
    }

    private List<Tag> generateCommonTags(MetricStdTags stdTags) {
        List<Tag> tags = new ArrayList<>();
        tags.add(Tag.of("appName", this.getAppName()));
        tags.add(Tag.of("url", stdTags.getUrl()));
        tags.add(Tag.of("side", stdTags.getSide()));
        if(MetricHelper.hasHost(stdTags)){
            tags.add(Tag.of("host",stdTags.getHost()));
        } else {
            tags.add(Tag.of("host","notneed"));
        }
        return tags;
    }




    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        if (StringUtils.isNotBlank(percent)) {
            percentiles = Symbols.SPLITTER.splitToStream(percent).mapToDouble(Double::parseDouble).toArray();
        } else {
            percentiles = null;
        }
        this.percent = percent;
    }


    public long getDecayOverTime() {
        return decayOverTime;
    }

    public void setDecayOverTime(long decayOverTime) {
        this.decayOverTime = decayOverTime;
    }

    public String getAppName() {
        return appName;
    }
}
