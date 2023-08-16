package com.jkqj.urlshorter;

/**
 * 随机字符串发生器
 * Created by luoguo on 2017/3/24.
 */
public interface StringGenerator {
    String generate(String url);

    void setLength(int length);
}
