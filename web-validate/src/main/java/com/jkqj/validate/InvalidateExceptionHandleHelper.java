package com.jkqj.validate;

import com.google.common.collect.Lists;
import com.jkqj.common.constants.Symbols;
import com.jkqj.common.enums.CommonErrorEnum;
import com.jkqj.common.result.Result;
import com.jkqj.validate.annotations.InvalidateCode;
import com.jkqj.validate.bean.InvalidateError;
import com.jkqj.validate.constant.Constants;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

/**
 * 处理Validate异常，根据异常抽取当前controller的方法，并从方法读取InvalidateCode注解，从而构建出友好的返回信息
 *
 * @author rolandhe
 */
public class InvalidateExceptionHandleHelper {

    private InvalidateExceptionHandleHelper() {
    }

    public static Result<Object> handle(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        MethodParameter parameter = e.getParameter();
        Method method = parameter.getMethod();
        InvalidateCode invalidateCode = method.getAnnotation(InvalidateCode.class);

        Result result = getResult(invalidateCode);
        result.setErrors(toMessageList(bindingResult.getFieldErrors()));

        return result;
    }

    public static Result<Object> handle(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> violationSet = e.getConstraintViolations();
        Method method = getValidatedMethod(violationSet);
        InvalidateCode invalidateCode = method.getAnnotation(InvalidateCode.class);

        Result result = getResult(invalidateCode);
        result.setErrors(toMessageList(violationSet));

        return result;
    }

    private static Method getValidatedMethod(Set<ConstraintViolation<?>> violationSet) {
        ConstraintViolation violation = violationSet.iterator().next();
        Class clazz = violation.getRootBeanClass();
        Path.Node node = violation.getPropertyPath().iterator().next();
        Method method = getMethodByName(clazz, node.getName());
        return method;
    }

    private static Result getResult(InvalidateCode invalidateCode) {
        Result result;
        if (invalidateCode == null || invalidateCode.value() == Integer.MIN_VALUE) {
            result = Result.fail(CommonErrorEnum.ARGUMENT_NOT_VALID);
        } else {
            result = Result.fail(invalidateCode.value(),
                    StringUtils.isEmpty(invalidateCode.message()) ? CommonErrorEnum.ARGUMENT_NOT_VALID.getDesc() : invalidateCode.message());
        }
        return result;
    }

    private static List<InvalidateError> toMessageList(Set<ConstraintViolation<?>> violationSet) {
        List<InvalidateError> errors = Lists.newArrayList();
        violationSet.forEach(constraintViolation -> {
            String fieldName = Constants.GLOBAL_EXCEPTION;
            String errorMsg = CommonErrorEnum.ARGUMENT_NOT_VALID.getDesc();
            Path propertyPath = constraintViolation.getPropertyPath();
            String[] pathArr = StringUtils.split(propertyPath.toString(), Symbols.POINT);
            InvalidateError invalidateError = new InvalidateError();
            if (pathArr != null) {
                fieldName = pathArr[1];
                errorMsg = constraintViolation.getMessageTemplate();
            }
            invalidateError.setFieldName(fieldName);
            invalidateError.setMessage(errorMsg);
            errors.add(invalidateError);
        });
        return errors;
    }

    private static List<InvalidateError> toMessageList(List<FieldError> fieldErrorList) {
        List<InvalidateError> errors = Lists.newArrayList();
        fieldErrorList.forEach(fieldError -> {
            InvalidateError invalidateError = new InvalidateError();
            invalidateError.setFieldName(fieldError.getField());
            invalidateError.setMessage(fieldError.getDefaultMessage());
            errors.add(invalidateError);
        });
        return errors;
    }

    private static Method getMethodByName(Class clazz, String methodName) {
        Method[] all = clazz.getMethods();
        for (Method m : all) {
            if (m.getName().equals(methodName)) {
                return m;
            }
        }
        throw new RuntimeException("不能找到方法:" + methodName + ", 是否是private");
    }
}
