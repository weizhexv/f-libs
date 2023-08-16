package com.jkqj.common.utils.json.ext;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.Converter;

/**
 * Long转换为字符串
 *
 * e.g:
 *
 * @JsonSerialize(converter = LongConverter.class)
 * private long id;
 *
 */
public class LongConverter implements Converter<Long,String> {
    @Override
    public String convert(Long value) {
        if(value == null) {
            return null;
        }
        return value.toString();
    }

    @Override
    public JavaType getInputType(TypeFactory typeFactory) {
        return typeFactory.constructType(Long.class);
    }

    @Override
    public JavaType getOutputType(TypeFactory typeFactory) {
        return typeFactory.constructType(String.class);
    }
}
