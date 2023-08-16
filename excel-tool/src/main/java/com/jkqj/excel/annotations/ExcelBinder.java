package com.jkqj.excel.annotations;

import java.lang.annotation.*;


/**
 * 对象属性绑定exel的列
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ExcelBinder {

//    /**
//     * 绑定excel的列序号，从0开始
//     *
//     * @return
//     */
//    int bindIndex();

    /**
     * 表格头文本
     *
     * @return
     */
    String headerText();

    /**
     *
     * 邦迪模式，读取还是输出
     *
     * @return
     */
    BinderMode binderMode() default BinderMode.ALL;

    /**
     * 日期格式
     *
     * @return
     */
    String dateFormatter() default "yyyy-MM-dd HH:mm:ss";

    /**
     * 对于number类型，如果excel文件对应的cell是空，是否可以自动转成0
     *
     * @return
     */
    boolean emptyAsZero() default false;
}
