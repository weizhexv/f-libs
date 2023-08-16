package com.jkqj.opensearch.sdk.jdbc.ext.types;

import org.opensearch.jdbc.types.TypeHelper;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class LocalDateType implements TypeHelper<LocalDate> {
    public static final LocalDateType INSTANCE = new LocalDateType();

    private LocalDateType() {

    }

    @Override
    public LocalDate fromValue(Object value, Map<String, Object> conversionParams) throws SQLException {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDate) {
            return (LocalDate) value;
        } else if (value instanceof String) {
            return LocalDate.parse((String) value, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }  else {
            throw objectConversionException(value);
        }
    }

    @Override
    public String getTypeName() {
        return "LocalDate";
    }
}