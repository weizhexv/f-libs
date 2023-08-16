package com.jkqj.dubbo.generic.reference;

/**
 * dubbo消费端通用配置
 *
 * @author rolandhe
 *
 */
public class GenericConsumerConfig {
    private String appName;
    private String registerAddress;

    private String protocol = "dubbo";

    private String threadPoolType="fixed";
    private Integer threads = 64;

    private String threadName = "dubbo-generic";
    private Integer timeout = 1000;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getRegisterAddress() {
        return registerAddress;
    }

    public void setRegisterAddress(String registerAddress) {
        this.registerAddress = registerAddress;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getThreadPoolType() {
        return threadPoolType;
    }

    public void setThreadPoolType(String threadPoolType) {
        this.threadPoolType = threadPoolType;
    }

    public Integer getThreads() {
        return threads;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }
}
