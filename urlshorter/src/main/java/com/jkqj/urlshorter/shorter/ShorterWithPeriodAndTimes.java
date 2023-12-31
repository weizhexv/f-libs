package com.jkqj.urlshorter.shorter;

import com.jkqj.urlshorter.ShorterGetter;

/**
 * 用来存储短地址，超时时间和访问次数
 * Created by luoguo on 2017/3/24.
 */
public class ShorterWithPeriodAndTimes implements ShorterGetter {
    private String shorter;
    private long period;
    private long times;
    public ShorterWithPeriodAndTimes() {
    }
    public ShorterWithPeriodAndTimes(String shorter, long period, long times) {
        setShorter(shorter);
        setTimes(times);
        setPeriod(period);
    }

    public long getTimes() {
        return times;
    }

    public void setTimes(long times) {
        this.times = times;
    }

    public String getShorter() {
        return shorter;
    }

    public void setShorter(String shorter) {
        this.shorter = shorter;
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

}
