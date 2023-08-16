package com.jkqj.urlshorter.shorter;

import com.jkqj.urlshorter.ShorterGetter;

/**
 * 存放短地址和密码
 * Created by luoguo on 2017/3/24.
 */
public class ShorterWithPassword implements ShorterGetter {
    private String shorter;
    private String password;

    public ShorterWithPassword() {
    }

    public ShorterWithPassword(String shorter, String password) {
        setShorter(shorter);
        setPassword(password);
    }

    public String getShorter() {
        return shorter;
    }

    public void setShorter(String shorter) {
        this.shorter = shorter;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
