package com.jkqj.dubbo.generic.http;


import com.jkqj.dubbo.generic.GenericCallContext;

public class GenericCallMapping {
    private String htpPath;
    private GenericCallContext callContext;

    public String getHtpPath() {
        return htpPath;
    }

    public void setHtpPath(String htpPath) {
        this.htpPath = htpPath;
    }

    public GenericCallContext getCallContext() {
        return callContext;
    }

    public void setCallContext(GenericCallContext callContext) {
        this.callContext = callContext;
    }
}
