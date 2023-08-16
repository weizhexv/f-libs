package com.jkqj.bt.web.controller;

import com.jkqj.bt.domain.City;
import com.jkqj.bt.service.CityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class DemoController {
    @Autowired
    private CityService cityService;

    @GetMapping("/")
    public String index() {
        return "root of demo";
    }

    @PostMapping("/city")
    public City saveCity(@RequestParam String country, @RequestParam String state, @RequestParam String name) {
        Long id = cityService.save(new City(null, name, state, country));
        log.debug("saved city with id {}", id);
        return cityService.find(id);
    }
}
