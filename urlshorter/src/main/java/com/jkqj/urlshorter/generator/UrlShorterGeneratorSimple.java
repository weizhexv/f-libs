package com.jkqj.urlshorter.generator;

import com.jkqj.urlshorter.ShorterStorage;
import com.jkqj.urlshorter.StringGenerator;
import com.jkqj.urlshorter.UrlShorterGenerator;
import com.jkqj.urlshorter.shorter.ShorterString;

/**
 * 用于生成指定长度的串
 * Created by luoguo on 2017/3/24.
 */
public class UrlShorterGeneratorSimple implements UrlShorterGenerator<ShorterString> {

    private StringGenerator generator;
    private ShorterStorage<ShorterString> shorterStorage;

    public ShorterStorage<ShorterString> getShorterStorage() {
        return shorterStorage;
    }

    public void setShorterStorage(ShorterStorage<ShorterString> shorterStorage) {
        this.shorterStorage = shorterStorage;
    }

    public StringGenerator getGenerator() {
        return generator;
    }

    public void setGenerator(StringGenerator generator) {
        this.generator = generator;
    }

    public ShorterString generate(String url) {
        String shorter = generator.generate(url);
        while (shorterStorage.get(shorter) != null) {
            shorter = generator.generate(url);
        }
        ShorterString newShorter = new ShorterString(shorter);
        shorterStorage.save(url, newShorter);
        return newShorter;
    }
}
