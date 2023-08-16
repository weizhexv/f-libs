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
@MappedTypes({Object.class})
@MappedJdbcTypes({JdbcType.VARCHAR})
public class JsonHandler extends BaseTypeHandler<Object> {
    private final Class<?> type;

    public JsonHandler(Class<?> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
//        TypeUtils.getTypeArguments((ParameterizedType) type.getGenericSuperclass());
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, Object object, JdbcType jdbcType) throws SQLException {
        log.debug("converting to json {}", object);
        preparedStatement.setString(i, JsonUtils.toJson(object));
    }

    @Override
    public Object getNullableResult(ResultSet resultSet, String columnName) throws SQLException {
        String json = resultSet.getString(columnName);
        log.debug("parsing json to {} {}", type, json);
        return StringUtils.isBlank(json) ? null : JsonUtils.toBean(json, type);
    }

    @Override
    public Object getNullableResult(ResultSet resultSet, int i) throws SQLException {
        String json = resultSet.getString(i);
        log.debug("parsing json to {} {}", type, json);
        return StringUtils.isBlank(json) ? null : JsonUtils.toBean(json, type);
    }

    @Override
    public Object getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        String json = callableStatement.getString(i);
        log.debug("parsing json to {} {}", type, json);
        return StringUtils.isBlank(json) ? null : JsonUtils.toBean(json, type);
    }
}
