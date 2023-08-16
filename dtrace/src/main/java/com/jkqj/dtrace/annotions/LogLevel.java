package com.jkqj.dtrace.annotions;

import lombok.Getter;

@Getter
public enum LogLevel {
    NONE(0),PARAM(1),RETURN(2),ALL(3);

    private final int value;


    LogLevel(int value) {
        this.value = value;
    }

    public  boolean hasParam() {
        return (this.value & PARAM.value) == PARAM.value;
    }

    public boolean hasReturn() {
        return (this.value & RETURN.value) == RETURN.value;
    }

    public static LogLevel ofIntValue(int value) {
        for(LogLevel ll : LogLevel.values()) {
            if(ll.value == value) {
                return ll;
            }
        }
        return null;
    }
}
