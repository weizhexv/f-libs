package com.jkqj.excel;

import com.jkqj.excel.annotations.BinderMode;
import com.jkqj.excel.helper.ExcelProcessHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * excel 处理工具, 最大能处理的行为100万
 *
 * @author rolandhe
 */
@Slf4j
public class ExcelProcessor {
    private static final int MAX_ROWS = 1000000;

    private static final int MAX_ROWS_BEFORE_HEADER = 100;

    private ExcelProcessor() {
    }

    /**
     * 从指定的excel文件中抽取对象列表
     *
     * @param excelFile
     * @param rowRecognizer
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> List<T> extractExcel(File excelFile, RowRecognizer rowRecognizer) throws IOException {
        try (InputStream inputStream = new FileInputStream(excelFile)) {
            return extractExcel(inputStream, rowRecognizer);
        }
    }

    /**
     * 从指定的excel文件流中抽取对象列表
     *
     * @param excelInputStream
     * @param rowRecognizer
     * @param <T>
     * @return
     * @throws IOException
     */
    public static <T> List<T> extractExcel(InputStream excelInputStream, RowRecognizer rowRecognizer) throws IOException {
        List<T> list = new ArrayList<>();
        Workbook wb = WorkbookFactory.create(excelInputStream);
        Sheet sheet = wb.getSheetAt(0);
        List<Row> emptyList = new ArrayList<>();
        int index = 0;

        boolean hasHeader = false;

        for (int i = index; i < MAX_ROWS; i++) {
            Row row = sheet.getRow(i);
            if (!hasHeader) {
                if(i >= MAX_ROWS_BEFORE_HEADER) {
                    log.warn("没有匹配到header,退出");
                    break;
                }
                if (row != null) {
                    hasHeader = rowRecognizer.isHeaderRow(row);
                    if (hasHeader) {
                        log.info("find header.");
                    }
                }
                continue;
            }
            boolean empty = rowRecognizer.isEmpty(row);
            if (empty) {
                emptyList.add(row);
                if (rowRecognizer.isEnd(emptyList)) {
                    break;
                }
                continue;
            }
            emptyList.clear();
            Object v = rowRecognizer.convertToEntityInstance(row);
            list.add((T) v);
        }
        return list;
    }


    /**
     * 输出对象到excel文件
     *
     * @param outExcel
     * @param list
     * @param <T>
     * @throws IOException
     */
    public static <T> void output2Excel(File outExcel, List<T> list) throws IOException {
        try (OutputStream outputStream = new FileOutputStream(outExcel)) {
            output2Excel(outputStream, list);
        }
    }

    /**
     * 输出对象到excel文件流
     *
     * @param outputStream
     * @param list
     * @param <T>
     * @throws IOException
     */
    public static <T> void output2Excel(OutputStream outputStream, List<T> list) throws IOException {
        if (list == null || list.size() == 0) {
            return;
        }
        List<BinderInfo> binderInfoList = ExcelProcessHelper.collectExcelBinder(list.get(0).getClass(), BinderMode.OUT);
        if (binderInfoList.size() == 0) {
            return;
        }
        int rowIndex = 0;
        int colIndex = 0;

        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet();

            // create header
            Row header = sheet.createRow(rowIndex++);
            for (BinderInfo binderInfo : binderInfoList) {
                Cell cell = header.createCell(colIndex++, CellType.STRING);
                cell.setCellValue(binderInfo.getExcelBinder().headerText());
            }
            // add data row
            for (T entity : list) {
                Row row = sheet.createRow(rowIndex++);
                colIndex = 0;
                for (BinderInfo binderInfo : binderInfoList) {
                    ExcelProcessHelper.addFieldValue2Cell(row, colIndex++, binderInfo, entity);
                }
            }
            // write to output steam
            wb.write(outputStream);
        }
    }
}
