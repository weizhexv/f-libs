package com.jkqj.urlshorter.shorter;

import com.jkqj.urlshorter.ShorterGetter;

/**
 * 存放短地址和访问次数
 * Created by luoguo on 2017/3/24.
 */
public class ShorterWithTimes implements ShorterGetter {
    private String shorter;
    private long times;

    public ShorterWithTimes() {
    }
    public ShorterWithTimes(String shorter, long times) {
        setShorter(shorter);
        setTimes(times);
    }

    public String getShorter() {
        return shorter;
    }

    public void setShorter(String shorter) {
        this.shorter = shorter;
    }

    public long getTimes() {
        return times;
    }

    public void setTimes(long times) {
        this.times = times;
    }

}
