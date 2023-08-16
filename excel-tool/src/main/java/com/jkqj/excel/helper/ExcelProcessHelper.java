package com.jkqj.excel.helper;

import com.jkqj.excel.BinderInfo;
import com.jkqj.excel.annotations.BinderMode;
import com.jkqj.excel.annotations.ExcelBinder;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 实体对象中对应绑定excel信息的工具
 *
 * @author rolandhe
 */
public class ExcelProcessHelper {
    private static final String DEFAULT_DATETIME_FORMATTER = "yyyy-MM-dd HH:mm:ss";
    private ExcelProcessHelper() {

    }

    /**
     * 收集对应行对象的绑定信息
     *
     * @param clazz
     * @return
     */
    public static List<BinderInfo> collectExcelBinder(Class<?> clazz, BinderMode binderMode) {
        List<BinderInfo> list = new ArrayList<>();

        Field[] fields = FieldUtils.getAllFields(clazz);
        Set<String> headerSet = new HashSet<>();
        for (Field field : fields) {
            ExcelBinder excelBinder = field.getAnnotation(ExcelBinder.class);
            if (excelBinder == null || !excelBinder.binderMode().isMatch(binderMode)) {
                continue;
            }

            if (headerSet.contains(excelBinder.headerText())) {
                String errorMessage = String.format("%s 属性对应的excel列重复。", field.getName());
                throw new RuntimeException(errorMessage);
            }
            headerSet.add(excelBinder.headerText());
            field.setAccessible(true);
            list.add(new BinderInfo(excelBinder, field));
        }

        return list;
    }

    /**
     * 从excel行中copy值到对应的对象
     *
     * @param row
     * @param entity
     * @param binderInfoList
     */
    public static void copyValueFromExcelRow(Row row, Object entity, final List<BinderInfo> binderInfoList) {
        for (BinderInfo binderInfo : binderInfoList) {
            ExcelBinder binder = binderInfo.getExcelBinder();
            Cell cell = row.getCell(binderInfo.getColumnIndex());

            Object targetValue = convertTetAsType(binderInfo.getField().getType(), cell, binder);
            try {
                FieldUtils.writeField(binderInfo.getField(), entity, targetValue);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 判断cell是否为空
     *
     * @param cell
     * @return
     */
    public static boolean isNotEmptyCell(Cell cell) {
        return cell != null && cell.getCellType() != CellType._NONE
                && cell.getCellType() != CellType.BLANK
                && cell.getCellType() != CellType.ERROR;
    }

    /**
     * 把对象的属性值写入cell
     *
     * @param row
     * @param colIndex
     * @param binderInfo
     * @param entity
     */
    public static void addFieldValue2Cell(Row row, int colIndex, BinderInfo binderInfo, Object entity) {
        try {
            Object value = FieldUtils.readField(binderInfo.getField(), entity, true);
            if (value == null) {
                return;
            }
            writeCellValue(value, row, colIndex, binderInfo);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeCellValue(Object value, Row row, int colIndex, BinderInfo binderInfo) {
        Class<?> type = binderInfo.getField().getType();

        if (type == Long.class || type == long.class) {
            Cell cell = row.createCell(colIndex, CellType.NUMERIC);
            cell.setCellValue((Long) value);
            return;
        }
        if (type == Integer.class || type == int.class) {
            Cell cell = row.createCell(colIndex, CellType.NUMERIC);
            cell.setCellValue((Integer) value);
            return;
        }
        if (type == Short.class || type == short.class) {
            Cell cell = row.createCell(colIndex, CellType.NUMERIC);
            cell.setCellValue((Short) value);
            return;
        }
        if (type == Byte.class || type == byte.class) {
            Cell cell = row.createCell(colIndex, CellType.NUMERIC);
            cell.setCellValue((Byte) value);
            return;
        }
        if (type == Double.class || type == double.class) {
            Cell cell = row.createCell(colIndex, CellType.NUMERIC);
            cell.setCellValue((Double) value);
            return;
        }
        if (type == Float.class || type == float.class) {
            Cell cell = row.createCell(colIndex, CellType.NUMERIC);
            cell.setCellValue((Float) value);
            return;
        }

        if (type == String.class) {
            Cell cell = row.createCell(colIndex, CellType.STRING);
            cell.setCellValue((String) value);
            return;
        }

        if (type == Boolean.class || type == boolean.class) {
            Cell cell = row.createCell(colIndex, CellType.BOOLEAN);
            cell.setCellValue((Boolean) value);
            return;
        }

        if (Date.class.isAssignableFrom(type)) {
            Cell cell = row.createCell(colIndex, CellType.STRING);
            String formatterString = binderInfo.getExcelBinder().dateFormatter();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatterString);
            cell.setCellValue(simpleDateFormat.format((Date) value));
            return;
        }

        if (LocalDateTime.class.isAssignableFrom(type)) {
            Cell cell = row.createCell(colIndex, CellType.STRING);
            String formatterString = binderInfo.getExcelBinder().dateFormatter();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatterString);
            cell.setCellValue(formatter.format((LocalDateTime) value));
            return;
        }

        if (LocalDate.class.isAssignableFrom(type)) {
            Cell cell = row.createCell(colIndex, CellType.STRING);
            String formatterString = binderInfo.getExcelBinder().dateFormatter();
            if(formatterString.equals(DEFAULT_DATETIME_FORMATTER)){
                formatterString = formatterString.substring(0,10);
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formatterString);
            cell.setCellValue(formatter.format((LocalDate) value));
            return;
        }

        if (LocalTime.class.isAssignableFrom(type)) {
            Cell cell = row.createCell(colIndex, CellType.STRING);
            String formatterString = binderInfo.getExcelBinder().dateFormatter();
            if(formatterString.equals(DEFAULT_DATETIME_FORMATTER)){
                formatterString = formatterString.substring(11);
            }
            cell.setCellValue(((LocalTime) value).format(DateTimeFormatter.ofPattern(formatterString)));
            return;
        }
        throw new RuntimeException("不识别的类型");
    }

    private static Object convertNumericTypeCell(Cell cell, Class<?> type) {
        double dlValue = cell.getNumericCellValue();
        if (type == Long.class || type == long.class) {
            return (long) (dlValue);
        }
        if (type == Integer.class || type == int.class) {
            return (int) (dlValue);
        }
        if (type == Short.class || type == short.class) {
            return (short) (dlValue);
        }
        if (type == Byte.class || type == byte.class) {
            return (byte) (dlValue);
        }
        if (type == Double.class || type == double.class) {
            return dlValue;
        }
        if (type == Float.class || type == float.class) {
            return (float) dlValue;
        }
        if (type == String.class) {
            if(cell instanceof XSSFCell) {
                return ((XSSFCell) cell).getRawValue();
            }
            return Double.toString(dlValue);
        }
        if (Date.class.isAssignableFrom(type)) {
            return cell.getDateCellValue();
        }

        if (LocalDateTime.class.isAssignableFrom(type)) {
            return cell.getLocalDateTimeCellValue();
        }

        if (LocalDate.class.isAssignableFrom(type)) {
            LocalDateTime localDateTime = cell.getLocalDateTimeCellValue();
            return localDateTime == null ? null : localDateTime.toLocalDate();
        }

        if (LocalTime.class.isAssignableFrom(type)) {
            LocalDateTime localDateTime = cell.getLocalDateTimeCellValue();
            return localDateTime == null ? null : localDateTime.toLocalTime();
        }

        throw new RuntimeException("不匹配的类型");
    }

    private static Object convertStringTypeCell(Cell cell, Class<?> type, String dateFormatter) {
        String text = cell.getStringCellValue();
        if (type == Long.class || type == long.class) {
            return Long.parseLong(text);
        }
        if (type == Integer.class || type == int.class) {
            return Integer.parseInt(text);
        }
        if (type == Short.class || type == short.class) {
            return Short.parseShort(text);
        }
        if (type == Byte.class || type == byte.class) {
            return Byte.parseByte(text);
        }
        if (type == Double.class || type == double.class) {
            return Double.parseDouble(text);
        }
        if (type == Float.class || type == float.class) {
            return Float.parseFloat(text);
        }
        if (type == String.class) {
            return text;
        }
        if (type == Boolean.class || type == boolean.class) {
            return Boolean.parseBoolean(text);
        }
        if (type.isAssignableFrom(Date.class)) {
            return parseDate(text, dateFormatter);
        }

        if (type.isAssignableFrom(LocalDateTime.class)) {
            return LocalDateTime.parse(text, DateTimeFormatter.ofPattern(dateFormatter));
        }

        if (type.isAssignableFrom(LocalDate.class)) {
            return LocalDate.parse(text, DateTimeFormatter.ofPattern(dateFormatter));
        }

        if (type.isAssignableFrom(LocalTime.class)) {
            return LocalTime.parse(text, DateTimeFormatter.ofPattern(dateFormatter));
        }
        throw new RuntimeException("不匹配的类型");
    }

    private static Object convertTetAsType(Class<?> type, Cell cell, ExcelBinder binder) {
        if (!isNotEmptyCell(cell)) {
            if (isNumber(type) && binder.emptyAsZero()) {
                return convertZeroNumber(type);
            }
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return convertNumericTypeCell(cell, type);
        }

        if (cell.getCellType() == CellType.STRING) {
            return convertStringTypeCell(cell, type, binder.dateFormatter());
        }

        if (cell.getCellType() == CellType.BOOLEAN) {
            if (type == Boolean.class || type == boolean.class) {
                return cell.getBooleanCellValue();
            }
            throw new RuntimeException("不匹配的类型");
        }


        throw new RuntimeException("不匹配的类型:" + type.getName());
    }

    private static Object convertZeroNumber(Class type) {
        String iText = "0";
        if (type == Long.class || type == long.class) {
            return Long.parseLong(iText);
        }
        if (type == Integer.class || type == int.class) {
            return Integer.parseInt(iText);
        }
        if (type == Short.class || type == short.class) {
            return Short.parseShort(iText);
        }
        if (type == Byte.class || type == byte.class) {
            return Byte.parseByte(iText);
        }
        String fText = "0.0";
        if (type == Double.class || type == double.class) {
            return Double.parseDouble(fText);
        }
        if (type == Float.class || type == float.class) {
            return Float.parseFloat(fText);
        }
        throw new RuntimeException("不支持的类型");
    }

    private static boolean isNumber(Class type) {
        if (type == Long.class || type == long.class
                || type == Integer.class || type == int.class
                || type == Short.class || type == short.class
                || type == Byte.class || type == byte.class
                || type == Double.class || type == double.class
                || type == Float.class || type == float.class) {
            return true;
        }
        return false;
    }

    private static Date parseDate(String text, String dateFormatter) {
        SimpleDateFormat simpleFormatter = new SimpleDateFormat(dateFormatter);
        try {
            return simpleFormatter.parse(text);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
