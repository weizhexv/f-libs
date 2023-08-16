package com.jkqj.opensearch.sdk.jdbc.ext;

import org.opensearch.jdbc.types.BaseTypeConverter;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MyVarcharTypeConverter extends BaseTypeConverter {
    private static final Set<Class> supportedJavaClasses = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList(
                    String.class, Timestamp.class, java.sql.Date.class,
                    Byte.class, Short.class, Integer.class, Long.class,
                    Boolean.class, LocalDateTime.class, LocalDate.class
            )));

    MyVarcharTypeConverter() {

    }

    @Override
    public Class getDefaultJavaClass() {
        return String.class;
    }

    @Override
    public Set<Class> getSupportedJavaClasses() {
        return supportedJavaClasses;
    }
}
