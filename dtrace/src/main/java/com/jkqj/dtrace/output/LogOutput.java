package com.jkqj.dtrace.output;

public interface LogOutput {
    String UNIFIED_BEAN_NAME = "unifiedLogOutput";

    void outputStart(String fullPath, Object[] args);

    void outputEnd(String fullPath, long cost, Object value, Throwable throwable);

    default void outputEndWithoutReturnValue(String fullPath, long cost, Object value, String fakeReturn, Throwable throwable) {
        this.outputEnd(fullPath, cost, fakeReturn, throwable);
    }

    default void outputStartWithoutParam(String fullPath, Object[] args, Object[] fakeParam) {
        this.outputStart(fullPath,fakeParam);
    }
}
