package com.jkqj.mybatis.extension.handler;

import com.jkqj.common.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@MappedTypes({JsonTypeBase.class})
@MappedJdbcTypes({JdbcType.VARCHAR})
public class JsonTypeHandler<E extends JsonTypeBase> extends BaseTypeHandler<E> {
    private final Class<E> type;

    public JsonTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, E object, JdbcType jdbcType) throws SQLException {
        log.debug("converting to json {}", object);
        preparedStatement.setString(i, JsonUtils.toJson(object));
    }

    @Override
    public E getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        String json = resultSet.getString(columnName);
        log.debug("parsing json {}", json);
        return StringUtils.isBlank(json) ? null : JsonUtils.toBean(json, type);
    }

    @Override
    public E getNullableResult(ResultSet resultSet, int i) throws SQLException {
        String json = resultSet.getString(i);
        log.debug("parsing json {}", json);
        return StringUtils.isBlank(json) ? null : JsonUtils.toBean(json, type);
    }

    @Override
    public E getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        String json = callableStatement.getString(i);
        log.debug("parsing json {}", json);
        return StringUtils.isBlank(json) ? null : JsonUtils.toBean(json, type);
    }
}
