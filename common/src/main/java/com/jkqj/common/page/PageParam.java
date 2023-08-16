package com.jkqj.common.page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页的参数
 * <p>
 * <br/>created by Tianxin on 2015年9月7日 下午3:29:01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PageParam implements Serializable {

    /**
     * 单页最大数
     */
    public static final int MAX_PAGE_NO = 100000;

    /**
     *
     */
    public static final int MAX_PAGE_SIZE = 10000;

    /**
     * 当前页码
     */
    private int pageNo = 1;

    /**
     * 每页多少条
     */
    private int pageSize = 20;

    /**
     * 排序字段
     */
    private String sort;

    /**
     * 正序还是倒序
     */
    private boolean asc = false;

    public void setPageNo(int pageNo) {
        if (pageNo > 0 && pageNo < MAX_PAGE_NO) {
            this.pageNo = pageNo;
        }
    }

    public void setPageSize(int pageSize) {
        if (pageSize > 0 && pageSize < MAX_PAGE_SIZE) {
            this.pageSize = pageSize;
        }
    }

    public void setPageSizeNoLimit(int pageSize) {
        if (pageSize > 0) {
            this.pageSize = pageSize;
        }
    }

    /**
     * 获取当前页第一条记录索引位置
     *
     * @return
     */
    public int getFirstResult() {
        return (pageNo - 1) * pageSize;
    }

    public String getSortAsc() {
        if (sort == null) {
            return null;
        }

        return sort + (asc ? "" : " desc");
    }

    /**
     * 根据分页参数获取当前页数据
     *
     * @param totalList 全量数据
     * @param <T>
     * @return
     */
    public <T> List<T> getSubList(List<T> totalList) {
        if (this.pageSize == -1 || (totalList == null || totalList.size() == 0)) {
            return totalList;
        }

        if (this.getFirstResult() > Math.min(totalList.size(), this.getFirstResult() + this.pageSize)) {
            return Collections.emptyList();
        }

        return totalList.subList(this.getFirstResult(), Math.min(totalList.size(),
                this.getFirstResult() + this.pageSize));
    }

}