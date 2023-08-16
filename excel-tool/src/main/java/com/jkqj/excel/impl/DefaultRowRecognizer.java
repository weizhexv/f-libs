package com.jkqj.excel.impl;

import com.jkqj.excel.BinderInfo;
import com.jkqj.excel.RowRecognizer;
import com.jkqj.excel.annotations.BinderMode;
import com.jkqj.excel.annotations.ExcelBinder;
import com.jkqj.excel.helper.ExcelProcessHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

/**
 * RowRecognizer缺省实现
 *
 * <p>
 * 当连续5行为空时，视为文件已经没有数据；
 *
 * </p>
 */
@Slf4j
public class DefaultRowRecognizer implements RowRecognizer {

    public static final int VALID_EMPTY_ROW_LIMIT = 5;
    public static final int MAX_COLUMNS = 2000;

    private final Class<?> rowEntityClass;
    private final List<BinderInfo> binderInfoList;
    private final Map<String, BinderInfo> binderInfoMap = new HashMap<>();


    /**
     * @param rowEntityClass 每行对应的实体类
     */
    public DefaultRowRecognizer(Class<?> rowEntityClass) {
        this.rowEntityClass = rowEntityClass;

        binderInfoList = ExcelProcessHelper.collectExcelBinder(this.rowEntityClass, BinderMode.IN);
        binderInfoList.forEach(new Consumer<BinderInfo>() {
            @Override
            public void accept(BinderInfo binderInfo) {
                binderInfoMap.put(binderInfo.getExcelBinder().headerText().toLowerCase(), binderInfo);
            }
        });
    }

    @Override
    public boolean isHeaderRow(Row row) {
        Map<String, Integer> matchedHeader = new HashMap<>();
        for (int i = 0; i < MAX_COLUMNS; i++) {
            Cell cell = row.getCell(i);
            if (cell == null || cell.getCellType() != CellType.STRING) {
                continue;
            }
            String textValue = cell.getStringCellValue();
            if (StringUtils.isEmpty(textValue)) {
                continue;
            }

            textValue = StringUtils.strip(textValue).trim().toLowerCase();
            ;
            if (binderInfoMap.containsKey(textValue)) {
                matchedHeader.put(textValue, i);
            }
            if (matchedHeader.size() == binderInfoMap.size()) {
                break;
            }
        }
        if (matchedHeader.size() != binderInfoMap.size()) {
            if (matchedHeader.size() > 0) {
                log.warn("不能完全匹配到header,缺失的列：{}", diff(matchedHeader.keySet(), binderInfoMap.keySet()));
            }
            return false;
        }
        for (Map.Entry<String, BinderInfo> entry : binderInfoMap.entrySet()) {
            entry.getValue().setColumnIndex(matchedHeader.get(entry.getKey()));
        }
        return true;
    }


    @Override
    public boolean isEmpty(Row row) {
        if (row == null) {
            return true;
        }
        for (BinderInfo binderInfo : binderInfoList) {
            Cell cell = row.getCell(binderInfo.getColumnIndex());
            if (!ExcelProcessHelper.isNotEmptyCell(cell)) {
                continue;
            }
            if (cell.getCellType() == CellType.STRING && StringUtils.isNotEmpty(cell.getStringCellValue())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isEnd(List<Row> emptyRows) {
        return emptyRows.size() >= VALID_EMPTY_ROW_LIMIT;
    }

    @Override
    public <T> T convertToEntityInstance(Row row) {
        try {
            Object rowEntity = rowEntityClass.getConstructor().newInstance();
            ExcelProcessHelper.copyValueFromExcelRow(row, rowEntity, binderInfoList);
            return (T) rowEntity;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private String diff(Set<String> factSet, Set<String> expectSet) {
        StringBuilder stringBuilder = new StringBuilder();
        expectSet.forEach(s -> {
            if (!factSet.contains(s)) {
                stringBuilder.append(s).append(",");
            }
        });
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

}
