package com.jkqj.validator.hibernate.ext;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CompanyIdConstraintValidator implements ConstraintValidator<CompanyId,Long> {
    @Override
    public boolean isValid(Long value, ConstraintValidatorContext context) {
        if(value == null){
            return false;
        }
        if(value == -10000L || value > 0){
            return true;
        }
        return false;
    }
}
