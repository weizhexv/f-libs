package com.jkqj.excel;

import com.jkqj.excel.impl.DefaultRowRecognizer;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.io.*;
import java.util.List;

public class ExcelTestcase {

    @Test
    public void test() throws IOException {
        DefaultRowRecognizer recognizer = new DefaultRowRecognizer(Entity.class);
        String path = "/Users/hexiufeng/Documents/company.xlsx";
        InputStream inputStream = new FileInputStream(path);

        List<Entity> entityList = ExcelProcessor.extractExcel(inputStream, recognizer);

        inputStream.close();
        if(entityList.size() == 0){
            return;
        }
        String target = "/Users/hexiufeng/Documents/company11.xlsx";

        OutputStream outputStream = new FileOutputStream(target);

        ExcelProcessor.output2Excel(outputStream, entityList);

        outputStream.close();

        System.out.println(entityList);

    }

    @Test
    public void test11() {
        String text = "     Bb1   ";
        String v =  StringUtils.strip(text).trim().toLowerCase();
        System.out.println(v);
    }
}
