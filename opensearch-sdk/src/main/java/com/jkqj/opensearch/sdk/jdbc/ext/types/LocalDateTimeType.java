package com.jkqj.opensearch.sdk.jdbc.ext.types;

import org.opensearch.jdbc.types.TypeHelper;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class LocalDateTimeType implements TypeHelper<LocalDateTime> {
    public static final LocalDateTimeType INSTANCE = new LocalDateTimeType();

    private LocalDateTimeType() {

    }

    @Override
    public LocalDateTime fromValue(Object value, Map<String, Object> conversionParams) throws SQLException {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDateTime) {
            return (LocalDateTime) value;
        } else if (value instanceof String) {
            return LocalDateTime.parse((String) value, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }  else {
            throw objectConversionException(value);
        }
    }

    @Override
    public String getTypeName() {
        return "LocalDateTime";
    }
}