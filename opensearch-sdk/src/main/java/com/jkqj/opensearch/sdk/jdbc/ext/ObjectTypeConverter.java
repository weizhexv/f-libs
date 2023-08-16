package com.jkqj.opensearch.sdk.jdbc.ext;

import lombok.extern.slf4j.Slf4j;
import org.opensearch.jdbc.types.TypeConverter;

import java.sql.SQLException;
import java.util.Map;

@Slf4j
public class ObjectTypeConverter implements TypeConverter {

    ObjectTypeConverter() {

    }


    @Override
    public <T> T convert(Object value, Class<T> clazz, Map<String, Object> conversionParams) throws SQLException {
        if (value != null) {
            log.info("object type value:{}", value);
        }
        return null;
    }
}
