package com.jkqj.sql.exec.context;

public class SQLExecutorContext {
    public static final ThreadLocal<Boolean> OUTPUT = ThreadLocal.withInitial(() -> false);
}
