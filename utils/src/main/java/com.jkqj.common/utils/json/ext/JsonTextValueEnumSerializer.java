package com.jkqj.common.utils.json.ext;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class JsonTextValueEnumSerializer extends JsonSerializer<JsonTextValueEnum> {

    @Override
    public void serialize(JsonTextValueEnum jsonTextValueEnum, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(jsonTextValueEnum.getTextValue());
    }

}
