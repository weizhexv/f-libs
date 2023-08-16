package com.jkqj.urlshorter.generator;

import com.jkqj.urlshorter.ShorterStorage;
import com.jkqj.urlshorter.StringGenerator;
import com.jkqj.urlshorter.UrlShorterGenerator;
import com.jkqj.urlshorter.shorter.ShorterWithPeriod;

/**
 * 用于生成指定长度的串,限制访问次数
 * Created by luoguo on 2017/3/24.
 */
public class UrlShorterGeneratorLimitPeriod implements UrlShorterGenerator<ShorterWithPeriod> {

    private StringGenerator generator;
    private ShorterStorage<ShorterWithPeriod> shorterStorage;
    /**
     * 有效时长，单位秒
     */
    private long period;

    public StringGenerator getGenerator() {
        return generator;
    }

    public void setGenerator(StringGenerator generator) {
        this.generator = generator;
    }

    public ShorterStorage<ShorterWithPeriod> getShorterStorage() {
        return shorterStorage;
    }

    public void setShorterStorage(ShorterStorage<ShorterWithPeriod> shorterStorage) {
        this.shorterStorage = shorterStorage;
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }


    public ShorterWithPeriod generate(String url) {
        String shorter = generator.generate(url);
        while (shorterStorage.get(shorter) != null) {
            shorter = generator.generate(url);
        }
        ShorterWithPeriod shorterWithPeriod = new ShorterWithPeriod(shorter, period);
        shorterStorage.save(url, shorterWithPeriod);
        return shorterWithPeriod;
    }

}
