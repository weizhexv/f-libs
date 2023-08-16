package com.jkqj.dtrace.output;

public interface MetricOutput {
    String UNIFIED_BEAN_NAME = "unifiedMetricOutput";

    String STD_INDICATOR_PERF = "jkqj.perf";

    String STD_INDICATOR_EXP_COUNTER = "jkqj.exp.counter";

    void metric(MetricStdTags stdTags,long cost,boolean hasExp);

    void counterExp(MetricStdTags stdTags);
}
