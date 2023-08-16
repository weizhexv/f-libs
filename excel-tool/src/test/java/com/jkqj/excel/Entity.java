package com.jkqj.excel;


import com.jkqj.excel.annotations.BinderMode;
import com.jkqj.excel.annotations.ExcelBinder;

import java.time.LocalDate;

public class Entity {
    @ExcelBinder(headerText = "公司全称")
    private String name;
    @ExcelBinder(headerText = "公司简称")
    private String shortName;

    @ExcelBinder(headerText = "公司英文名")
    private String enName;

    @ExcelBinder(headerText = "公司网址")
    private String website;

    @ExcelBinder(headerText = "企业性质")
    private String kind;

    @ExcelBinder(headerText = "公司二级行业")
    private String industryName;

    @ExcelBinder(headerText = "二级行业编码")
    private String industry;

    @ExcelBinder(headerText = "融资阶段")
    private String finStage;

    @ExcelBinder(headerText = "公司规模")
    private String scale;

//    @ExcelBinder(headerText = "联系人")
//    private String contacts;

    @ExcelBinder(headerText = "省")
    private String province;

    @ExcelBinder(headerText = "市")
    private String city;

    @ExcelBinder(headerText = "区/县")
    private String district;

    @ExcelBinder(headerText = "详细地址")
    private String address;

//    @ExcelBinder(headerText = "公司状态")
//    private String status;
//
//    @ExcelBinder(headerText = "注册时间")
//    private LocalDate issueDate;

    @ExcelBinder(headerText = "失败原因", binderMode = BinderMode.OUT)
    private String failedReason;

}
