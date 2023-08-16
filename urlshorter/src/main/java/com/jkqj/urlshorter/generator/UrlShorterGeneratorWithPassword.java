package com.jkqj.urlshorter.generator;

import com.jkqj.urlshorter.ShorterStorage;
import com.jkqj.urlshorter.StringGenerator;
import com.jkqj.urlshorter.UrlShorterGenerator;
import com.jkqj.urlshorter.shorter.ShorterWithPassword;

/**
 * 用于生成指定长度的串
 * Created by luoguo on 2017/3/24.
 */
public class UrlShorterGeneratorWithPassword implements UrlShorterGenerator<ShorterWithPassword> {

    private StringGenerator shorterGenerator;
    private StringGenerator passwordGenerator;
    private ShorterStorage<ShorterWithPassword> shorterStorage;

    public StringGenerator getShorterGenerator() {
        return shorterGenerator;
    }

    public void setShorterGenerator(StringGenerator shorterGenerator) {
        this.shorterGenerator = shorterGenerator;
    }

    public StringGenerator getPasswordGenerator() {
        return passwordGenerator;
    }

    public void setPasswordGenerator(StringGenerator passwordGenerator) {
        this.passwordGenerator = passwordGenerator;
    }

    public ShorterStorage<ShorterWithPassword> getShorterStorage() {
        return shorterStorage;
    }

    public void setShorterStorage(ShorterStorage<ShorterWithPassword> shorterStorage) {
        this.shorterStorage = shorterStorage;
    }


    public ShorterWithPassword generate(String url) {
        String shorter = shorterGenerator.generate(url);
        while (shorterStorage.get(shorter) != null) {
            shorter = shorterGenerator.generate(url);
        }
        ShorterWithPassword shorterWithPassword = new ShorterWithPassword(shorter, passwordGenerator.generate(url));
        shorterStorage.save(url, shorterWithPassword);
        return shorterWithPassword;
    }


}
