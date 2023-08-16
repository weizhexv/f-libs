package com.jkqj.urlshorter.generator;

import com.jkqj.urlshorter.ShorterStorage;
import com.jkqj.urlshorter.StringGenerator;
import com.jkqj.urlshorter.UrlShorterGenerator;
import com.jkqj.urlshorter.shorter.ShorterWithTimes;

/**
 * 用于生成指定长度的串,限制访问次数
 * Created by luoguo on 2017/3/24.
 */
public class UrlShorterGeneratorLimitTimes implements UrlShorterGenerator<ShorterWithTimes> {

    private StringGenerator generator;
    private ShorterStorage<ShorterWithTimes> shorterStorage;
    /**
     * 有效时长，单位秒
     */
    private long times;

    public StringGenerator getGenerator() {
        return generator;
    }

    public void setGenerator(StringGenerator generator) {
        this.generator = generator;
    }

    public ShorterStorage<ShorterWithTimes> getShorterStorage() {
        return shorterStorage;
    }

    public void setShorterStorage(ShorterStorage<ShorterWithTimes> shorterStorage) {
        this.shorterStorage = shorterStorage;
    }

    public long getTimes() {
        return times;
    }

    public void setTimes(long times) {
        this.times = times;
    }


    public ShorterWithTimes generate(String url) {
        String shorter = generator.generate(url);
        while (shorterStorage.get(shorter) != null) {
            shorter = generator.generate(url);
        }
        ShorterWithTimes shorterWithPeriod = new ShorterWithTimes(shorter, times);
        shorterStorage.save(url, shorterWithPeriod);
        return shorterWithPeriod;
    }

}
