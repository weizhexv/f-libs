package com.jkqj.eda;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum EventStatus {
    UNPUBLISHED(0),
    PUBLISHED(1);

    @Getter
    private final int code;
}
