package com.jkqj.common.result;

import com.jkqj.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 结果
 *
 * @author cb
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> implements Serializable {
    private static final long serialVersionUID = -828357066742441358L;

    private boolean success = true;

    private T data;

    private Integer code;

    private String message;

    private T errors;

    private static final Result<Void> SUCCESS = new Result();

    static {
        SUCCESS.setCode(200);
    }

    public Result(T data) {
        this.data = data;
    }

    public Result(boolean success, Integer code, String message) {
        this.success = success;
        this.code = code;
        this.message = message;
    }

    public Result(boolean success, Integer code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Result(boolean success, Integer code, T errors) {
        this.success = success;
        this.code = code;
        this.errors = errors;
    }

    public static Result<Void> success() {
        return SUCCESS;
    }

    public static <T> Result<T> success(T t) {
        return new Result<>(t);
    }

    public static Result<Map<String, Object>> success(String key, Object value) {
        Map<String, Object> map = new HashMap<>(1);
        map.put(key, value);

        return new Result<>(map);
    }

    public static <T> Result<T> fail(String message) {
        return fail(1, message);
    }

    public static <T> Result<T> fail(Integer code, String message) {
        return new Result<>(false, code, message);
    }

    public static <T> Result<T> fail(Integer code, String message, T data) {
        return new Result<>(false, code, message, data);
    }

    public static <T> Result<T> fail(BaseEnum baseEnum) {
        return new Result<>(false, baseEnum.getCode(), baseEnum.getDesc());
    }

    public static <T> Result<T> fail(BaseEnum baseEnum, String message) {
        return new Result<>(false, baseEnum.getCode(), message);
    }

    public static <T> Result<T> fail(Result result) {
        return new Result<>(false, result.getCode(), result.getMessage());
    }

    public static <T> Result<T> fail(Integer code, T errors) {
        return new Result<>(false, code, errors);
    }

    public static <T> Result<T> fail(BaseEnum baseEnum, T errors) {
        return new Result<>(false, null, baseEnum.getCode(), baseEnum.getDesc(), errors);
    }

}