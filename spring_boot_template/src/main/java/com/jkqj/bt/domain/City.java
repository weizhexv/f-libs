package com.jkqj.bt.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class City {
    private Long id;
    private String name;
    private String state;
    private String country;
}
