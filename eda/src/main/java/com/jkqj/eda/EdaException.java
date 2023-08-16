package com.jkqj.eda;

import java.util.List;

public class EdaException extends RuntimeException {
    public EdaException() {
    }

    public EdaException(String message, Object... args) {
        this(message, null, args);
    }

    public EdaException(String message, Throwable cause, Object... args) {
        super(ExceptionUtils.format(message, args), cause);
    }

    public EdaException(Throwable cause) {
        super(cause);
    }

    public EdaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public EdaException(List<String> messages) {
        this(messages, null);
    }

    public EdaException(List<String> messages, Throwable cause) {
        this(ExceptionUtils.flatten(messages), cause);
    }

}
