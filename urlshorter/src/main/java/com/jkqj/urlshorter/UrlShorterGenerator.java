package com.jkqj.urlshorter;

/**
 *
 * <p>
 * Created by luoguo on 2017/3/24.
 */
public interface UrlShorterGenerator<T extends ShorterGetter> {


    /**
     * 产生一个短链接对象
     *
     * @param url
     * @return
     */
    T generate(String url);

}
