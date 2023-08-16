package com.jkqj.common.utils.json.ext;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * 不包含秒的转换
 * e.g:
 *
 *  @JsonSerialize(using = ShortLocalDateTimeSerializer.class)
 *  @JsonDeserialize(using = ShortLocalDatetimeDeserializer.class)
 */
public class ShortLocalDatetimeDeserializer extends JsonDeserializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctx)
            throws IOException {
        String str = p.getText();
        LocalDateTime localDateTime =  LocalDateTime.parse(str, ShortLocalDateTimeSerializer.DATE_FORMATTER);
        return localDateTime;
    }
}
