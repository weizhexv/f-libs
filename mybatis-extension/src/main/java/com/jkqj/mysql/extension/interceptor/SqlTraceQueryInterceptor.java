package com.jkqj.mysql.extension.interceptor;

import com.jkqj.sql.exec.context.SQLExecutorContext;
import com.mysql.cj.MysqlConnection;
import com.mysql.cj.Query;
import com.mysql.cj.conf.HostInfo;
import com.mysql.cj.interceptors.QueryInterceptor;
import com.mysql.cj.log.Log;
import com.mysql.cj.protocol.Resultset;
import com.mysql.cj.protocol.ServerSession;
import lombok.extern.slf4j.Slf4j;

import java.util.Properties;
import java.util.function.Supplier;

/**
 * 在jdbc驱动层面跟踪sql的执行情况
 *
 * @author relandhe
 */
@Slf4j
public class SqlTraceQueryInterceptor implements QueryInterceptor {
    private static final ThreadLocal<Long> CONTEXT = ThreadLocal.withInitial(() -> null);

    @Override
    public QueryInterceptor init(MysqlConnection conn, Properties props, Log log) {
        return this;
    }

    @Override
    public <T extends Resultset> T preProcess(Supplier<String> sql, Query interceptedQuery) {
        if (filter(sql)) {
            return null;
        }
        CONTEXT.set(System.currentTimeMillis());
        return null;
    }

    @Override
    public boolean executeTopLevelOnly() {
        return false;
    }

    @Override
    public void destroy() {

    }

    @Override
    public <T extends Resultset> T postProcess(Supplier<String> sql, Query interceptedQuery, T originalResultSet, ServerSession serverSession) {
        if (filter(sql)) {
            return null;
        }
        long start = CONTEXT.get();

        HostInfo hostInfo = interceptedQuery.getSession().getHostInfo();
        if( SQLExecutorContext.OUTPUT.get()) {
            log.info("[sql-trace]-cost {}ms for {} from {}", System.currentTimeMillis() - start, sql.get(), buildHostInfo(hostInfo));
        }
        return null;
    }

    private boolean filter(Supplier<String> sql) {
        String rawSQL = sql.get();
        if (rawSQL.equalsIgnoreCase("commit")) {
            return true;
        }
        if (rawSQL.equalsIgnoreCase("rollback")) {
            return true;
        }
        int sampleLen = 10;
        String sample = rawSQL;
        if (rawSQL.length() > sampleLen) {
            sample = rawSQL.substring(0, sampleLen);
        }
        sample = sample.toLowerCase();
        return sample.startsWith("set ");
    }

    private String buildHostInfo(HostInfo hostInfo) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(hostInfo.getHostPortPair())
                .append(",")
                .append(hostInfo.getUser())
                .append(",")
                .append(maskPassword(hostInfo.getPassword()))
                .append(",")
                .append(hostInfo.getDatabase());
        return stringBuilder.toString();
    }

    private String maskPassword(String password) {
        if (password == null || password.length() <= 2) {
            return "**********";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(password.substring(0, 1));
        for (int i = 0; i < password.length() - 2; i++) {
            stringBuilder.append("*");
        }
        stringBuilder.append(password.substring(password.length() - 1));
        return stringBuilder.toString();
    }
}
