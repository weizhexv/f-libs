package com.jkqj.opensearch.sdk.jdbc.ext;

import com.jkqj.opensearch.sdk.jdbc.ext.types.LocalDateTimeType;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.opensearch.jdbc.types.BaseTypeConverter;
import org.opensearch.jdbc.types.TypeConverter;
import org.opensearch.jdbc.types.TypeConverters;
import org.opensearch.jdbc.types.TypeHelper;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.sql.JDBCType;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
public class JdbcDatasourceInitializer {
    static {
        try {
            Map<Class, TypeHelper> mapTypeHelperMap = (Map<Class, TypeHelper>) FieldUtils.readStaticField(BaseTypeConverter.class, "typeHandlerMap", true);

            mapTypeHelperMap.put(LocalDateTime.class, LocalDateTimeType.INSTANCE);

            Map<JDBCType, TypeConverter> tcMap = (Map<JDBCType, TypeConverter>) FieldUtils.readStaticField(TypeConverters.class, "tcMap", true);
            tcMap.put(JDBCType.VARCHAR, new MyVarcharTypeConverter());
            tcMap.put(JDBCType.JAVA_OBJECT, new ObjectTypeConverter());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private HikariDataSource dataSource;
    private final OsJdbcConfig osConfig;

    public JdbcDatasourceInitializer(OsJdbcConfig osConfig) {
        this.osConfig = osConfig;
    }


    @PostConstruct
    public void init() {
        dataSource = buildDatasource(osConfig);
    }

    @PreDestroy
    public synchronized void destroy() {
        if (dataSource == null) {
            return;
        }
        dataSource.close();
        dataSource = null;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    private HikariDataSource buildDatasource(OsJdbcConfig osConfig) {
        HikariConfig config = new HikariConfig();
        config.setDriverClassName("org.opensearch.jdbc.DriverExt");
        config.setReadOnly(true);
        config.setAutoCommit(true);
        config.setJdbcUrl(osConfig.getUrl());
        config.setUsername(osConfig.getUsername());
        config.setPassword(osConfig.getPassword());
        config.setConnectionTimeout(osConfig.getConnectionTimeout());
        config.setIdleTimeout(osConfig.getIdleTimeout());
        config.setMaximumPoolSize(osConfig.getMaximumPoolSize());
        config.setMinimumIdle(osConfig.getMinimumIdle());
        config.setMaxLifetime(osConfig.getMaxLifetime());
//        config.addDataSourceProperty("fetchSize", osConfig.getFetchSize());
//        config.addDataSourceProperty("useSSL", "true");
        config.addDataSourceProperty("trustSelfSigned", "true");
        config.setConnectionTestQuery("select 1");

        return new HikariDataSource(config);
    }

}
