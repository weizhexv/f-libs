package com.jkqj.bt.service.impl;

import com.jkqj.bt.dal.mapper.CityMapper;
import com.jkqj.bt.domain.City;
import com.jkqj.bt.service.CityService;
import io.micrometer.core.instrument.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.google.common.base.Preconditions.checkArgument;

@Service
public class CityServiceImpl implements CityService {
    @Autowired
    private CityMapper mapper;

    @Override
    public Long save(City city) {
        checkArgument(city != null);
        checkArgument(StringUtils.isNotBlank(city.getName()));
        checkArgument(StringUtils.isNotBlank(city.getCountry()));
        checkArgument(StringUtils.isNotBlank(city.getState()));

        mapper.insert(city);
        return city.getId();
    }

    @Override
    public City find(Long id) {
        checkArgument(id != null);
        checkArgument(id > 0);

        return mapper.findById(id);
    }
}
