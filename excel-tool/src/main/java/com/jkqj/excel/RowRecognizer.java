package com.jkqj.excel;

import org.apache.poi.ss.usermodel.Row;

import java.util.List;

/**
 * 行信息识别工具
 *
 * @author rolandhe
 *
 */
public interface RowRecognizer {
    /**
     * header
     *
     * @param row
     * @return
     */
    boolean isHeaderRow(Row row);

    /**
     * 是否是数据行
     *
     * @param row
     * @return
     */
    boolean isEmpty(Row row);

    /**
     * 是否可以结束
     *
     * @param emptyRows
     * @return
     */
    boolean isEnd(List<Row> emptyRows);

    /**
     *
     * 转换一行数据为一个实体对象
     *
     * @param row
     * @param <T>
     * @return
     */
    <T> T convertToEntityInstance(Row row);
}
