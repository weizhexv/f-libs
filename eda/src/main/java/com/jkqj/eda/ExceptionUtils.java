package com.jkqj.eda;

import org.slf4j.helpers.MessageFormatter;

import java.util.List;

public final class ExceptionUtils {
    public static String flatten(List<String> errors) {
        return errors.stream()
                .reduce((result, element) -> String.format("%s\n%s", result, element))
                .orElse("");
    }

    public static String format(String messagePattern, Object... args) {
        return MessageFormatter.arrayFormat(messagePattern, args).getMessage();
    }
}
