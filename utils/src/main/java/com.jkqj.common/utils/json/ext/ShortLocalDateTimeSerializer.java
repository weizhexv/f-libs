package com.jkqj.common.utils.json.ext;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 不包含秒的转换
 * e.g:
 *
 *  @JsonSerialize(using = ShortLocalDateTimeSerializer.class)
 *  @JsonDeserialize(using = ShortLocalDatetimeDeserializer.class)
 */
public class ShortLocalDateTimeSerializer extends JsonSerializer<LocalDateTime> {
    static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        String s = value.format(DATE_FORMATTER);
        gen.writeString(s);
    }
}
