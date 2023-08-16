package org.opensearch.jdbc;

import com.amazonaws.opensearch.sql.jdbc.shadow.org.apache.http.Header;
import com.amazonaws.opensearch.sql.jdbc.shadow.org.apache.http.client.methods.CloseableHttpResponse;
import com.jkqj.opensearch.sdk.json.JsonHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.opensearch.jdbc.config.ConnectionConfig;
import org.opensearch.jdbc.logging.LogLevel;
import org.opensearch.jdbc.logging.Logger;
import org.opensearch.jdbc.logging.LoggingSource;
import org.opensearch.jdbc.protocol.http.JsonHttpProtocolFactory;
import org.opensearch.jdbc.transport.TransportException;
import org.opensearch.jdbc.transport.TransportFactory;
import org.opensearch.jdbc.transport.http.ApacheHttpTransport;
import org.opensearch.jdbc.transport.http.HttpParam;
import org.opensearch.jdbc.transport.http.HttpTransport;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
class ConnectionImplExt extends ConnectionImpl {
    public ConnectionImplExt(ConnectionConfig connectionConfig, Logger log) throws SQLException {
        super(connectionConfig, ApacheHttpTransportFactoryExt.INSTANCE, JsonHttpProtocolFactory.INSTANCE, log);
    }

    private static class ApacheHttpTransportFactoryExt implements TransportFactory<ProxyTransport> {
        public static final ApacheHttpTransportFactoryExt INSTANCE = new ApacheHttpTransportFactoryExt();

        private ApacheHttpTransportFactoryExt() {
        }

        @Override
        public ProxyTransport getTransport(ConnectionConfig config, Logger log, String userAgent) throws TransportException {
            return new ProxyTransport(new ApacheHttpTransport(config, log, userAgent));
        }
    }

    private static class ProxyTransport implements HttpTransport, LoggingSource {
        private final ApacheHttpTransport apacheHttpTransport;

        private ProxyTransport(ApacheHttpTransport apacheHttpTransport) {
            this.apacheHttpTransport = apacheHttpTransport;
        }

        @Override
        public CloseableHttpResponse doGet(String path, Header[] headers, HttpParam[] httpParams, int timeout) throws TransportException {
            if (log.isInfoEnabled() && !"/".equals(path)) {
                log.info("os jdbc: start-- get path:{},timeout:{}", path, timeout);
            }
            long start = System.currentTimeMillis();
            CloseableHttpResponse closeableHttpResponse;
            try {
                closeableHttpResponse = apacheHttpTransport.doGet(path, headers, httpParams, timeout);
                return closeableHttpResponse;
            } finally {
                if (!"/".equals(path)) {
                    log.info("os jdbc: end--get path:{},cost:{} ms", path, System.currentTimeMillis() - start);
                }
            }
        }

        @Override
        public CloseableHttpResponse doPost(String path, Header[] headers, HttpParam[] httpParams, String body, int timeout) throws TransportException {
            Map<String, Object> bodyMap = JsonHolder.fromJson(body);
            String querySql = (String) bodyMap.get("query");
            if (querySql != null && "SELECT 1".equalsIgnoreCase(querySql)) {
//                bodyMap.remove("fetch_size");
                body = JsonHolder.toNormalJson(bodyMap);
            }

            String newBody = applyBodyEnv(querySql, bodyMap);

            if (log.isInfoEnabled() && !"/".equals(path)) {
                Map<String, Object> params = new HashMap<>();
                params.put("body", body);
                params.put("timeout", timeout);
                log.info("os jdbc: start--path:{}, p:{}", path, JsonHolder.toNormalJson(params));
            }

            long start = System.currentTimeMillis();
            CloseableHttpResponse closeableHttpResponse;
            try {
                closeableHttpResponse = apacheHttpTransport.doPost(path, headers, httpParams, newBody == null ? body : newBody, timeout);
                return closeableHttpResponse;
            } finally {
                if (!"/".equals(path)) {
                    log.info("os jdbc: end-- post path:{},cost:{} ms", path, System.currentTimeMillis() - start);
                }
            }
        }

        private String applyBodyEnv(String querySql, Map<String, Object> bodyMap) {
            if (querySql == null||!querySql.contains("@[env].")) {
                return null;
            }

            bodyMap.put("query", StringUtils.replace(querySql, "@[env].", DriverExt.ENV));


            return JsonHolder.toNormalJson(bodyMap);
        }

        @Override
        public void close() throws TransportException {
            apacheHttpTransport.close();
        }

        @Override
        public void setReadTimeout(int timeout) {
            apacheHttpTransport.setReadTimeout(timeout);
        }

        @Override
        public String logMessage(String format, Object... args) {
            return apacheHttpTransport.logMessage(format, args);
        }

        @Override
        public String logMessage(String message) {
            return apacheHttpTransport.logMessage(message);
        }

        @Override
        public String logEntry(String format, Object... args) {
            return apacheHttpTransport.logEntry(format, args);
        }

        @Override
        public String logExit(String message, Object returnValue) {
            return apacheHttpTransport.logExit(message, returnValue);
        }

        @Override
        public String logExit(String message) {
            return apacheHttpTransport.logExit(message);
        }

        @Override
        public String getSource() {
            return apacheHttpTransport.getSource();
        }

        @Override
        public void logAndThrowSQLException(Logger log, SQLException sqlex) throws SQLException {
            apacheHttpTransport.logAndThrowSQLException(log, sqlex);
        }

        @Override
        public void logAndThrowSQLException(Logger log, LogLevel severity, SQLException sqlex) throws SQLException {
            apacheHttpTransport.logAndThrowSQLException(log, severity, sqlex);
        }

        @Override
        public void logAndThrowSQLException(Logger log, LogLevel severity, String message, SQLException sqlex) throws SQLException {
            apacheHttpTransport.logAndThrowSQLException(log, severity, message, sqlex);
        }

        @Override
        public String buildMessage(String message) {
            return apacheHttpTransport.buildMessage(message);
        }
    }
}
