package com.jkqj.validate;

import com.jkqj.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;

/**
 * 全局处理validate失败异常基类，要想使用需要继承。
 * <p>
 * spring的验证分为3中模式,每一种模式实现的方式不同，返回的异常也不同:
 * <ul>
 * <ui>public String valid(@Valid @NotNull(message = "toUser参数不能为空") String toUser)，需要在Controller加@Validate注解，在MethodValidationInterceptor实现，返回ConstraintViolationException异常</ui>
 * <ui>public String valid1(@RequestBody @Validated User user)，在RequestResponseBodyMethodProcessor实现，返回MethodArgumentNotValidException异常</ui>
 * <ui>public String valid2(@Validated User user)，在ServletModelAttributeMethodProcessor实现，返回BindException异常</ui>
 * </ul>
 * 第二、三种是在DataBinder中实现，
 * 第三种BindException，我们会使用 ValidatedFriendlyWebMvcConfig 来转换为MethodArgumentNotValidException异常。
 * <p>
 * <p>
 * e.g.
 *
 * @RestControllerAdvice public class CommonExceptionHandler extends GlobalInvalidateExceptionHandler {
 * <p>
 * }
 */
@Slf4j
public abstract class GlobalInvalidateExceptionHandler {
    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result<Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("method argument not valid exception:{}", ex.getMessage());
        return InvalidateExceptionHandleHelper.handle(ex);
    }

    @ExceptionHandler({ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Result<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("constraint violation exception:{}", ex.getMessage());
        return InvalidateExceptionHandleHelper.handle(ex);
    }
}
