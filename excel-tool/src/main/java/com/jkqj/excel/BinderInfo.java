package com.jkqj.excel;

import com.jkqj.excel.annotations.ExcelBinder;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;

/**
 * 描述对象绑定excel的信息
 *
 */
@Setter
@Getter
public class BinderInfo {
    /**
     * 绑定注解信息
     */
    private final ExcelBinder excelBinder;

    /**
     * 绑定excel列信息的field
     */
    private final Field field;

    private int columnIndex;

    public BinderInfo(ExcelBinder excelBinder, Field field) {
        this.excelBinder = excelBinder;
        this.field = field;
    }
}
