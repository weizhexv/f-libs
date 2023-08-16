package com.jkqj.bt;

import com.jkqj.bt.config.MyBatisConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
public class BootTemplateApplication {
    @Autowired
    private MyBatisConfig myBatisConfig;

    public static void main(String[] args) {
        SpringApplication.run(BootTemplateApplication.class, args);
    }
}

