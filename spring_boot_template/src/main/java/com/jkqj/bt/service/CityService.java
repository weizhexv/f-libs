package com.jkqj.bt.service;

import com.jkqj.bt.domain.City;

public interface CityService {
    Long save(City city);

    City find(Long id);
}
