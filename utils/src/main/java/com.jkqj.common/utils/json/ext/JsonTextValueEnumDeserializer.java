package com.jkqj.common.utils.json.ext;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.rmi.RemoteException;


public class JsonTextValueEnumDeserializer extends JsonDeserializer<JsonTextValueEnum> {

    @Override
    public JsonTextValueEnum deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {

        Object entity =  p.getParsingContext().getCurrentValue();
        String fieldName = p.getParsingContext().getCurrentName();

        Field field = FieldUtils.getField(entity.getClass(),fieldName,true);
        Class<?> clazz =  field.getType();
        if(!clazz.isEnum() || !JsonTextValueEnum.class.isAssignableFrom(clazz)){
            throw new RemoteException("类型错误,不能使用JsonTextValueEnumDeserializer");
        }
        String text =  p.getText();
        Object[] values =  clazz.getEnumConstants();
        for(Object v : values) {
            JsonTextValueEnum jte = (JsonTextValueEnum) v;
            if(jte.getTextValue().equals(text)){
                return jte;
            }
        }

        return null;
    }
}
