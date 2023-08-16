package com.jkqj.dubbo.common.timeout;

public interface MethodTimeoutConfProvider {
    DubboMethodConf getMethodTimeout(String methodUrl);

    default void setupOnceTimeout(Integer timeout,Integer retries){

    }
   default void clearOnceTimeout(){

   }
}
