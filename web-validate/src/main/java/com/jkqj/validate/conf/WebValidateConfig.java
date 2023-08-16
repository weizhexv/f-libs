package com.jkqj.validate.conf;

import com.jkqj.validate.filter.ExceptionHandlerFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebValidateConfig {

    @Bean
    public ValidatedFriendlyWebMvcConfig validatedFriendlyWebMvcConfig(){
        return new ValidatedFriendlyWebMvcConfig();
    }

    @Bean
    public ExceptionHandlerFilter exceptionHandlerFilter(){
        return new ExceptionHandlerFilter();
    }

}