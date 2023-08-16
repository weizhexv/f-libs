package com.jkqj.urlshorter;

/**
 * 用来存储字符串短地址,针对不同的generator需要有不同的存储器
 * Created by luoguo on 2017/3/24.
 */
public interface ShorterStorage<T extends ShorterGetter> {

    String get(String shorter);

    void clean(String url);

    void cleanShorter(String shorter);

    void save(String url, T shorter);

    void clean();

}
