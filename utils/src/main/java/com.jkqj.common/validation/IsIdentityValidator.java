package com.jkqj.common.validation;

import com.jkqj.common.utils.ValidationUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author xuweizhe@reta-inc.com
 * @date 2022/4/11
 * @description
 */
public class IsIdentityValidator implements ConstraintValidator<IsIdentity, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return ValidationUtils.isIdentity(value);
    }
}
