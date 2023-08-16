package com.jkqj.common.page;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;

/**
 * 分页对象
 *
 * @param <T>
 * @author cb
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageList<T> implements Serializable {
    private static final long serialVersionUID = -3355049623232655831L;

    protected int pageNo = 1;
    protected int pageSize = -1;
    protected long totalCount = -1;

    /**
     * 排序字段,无默认值. 多个排序字段时用','分隔.
     */
    protected String orderBy = null;

    protected List<T> list = new ArrayList<>();

    protected Map<String, Object> extra;

    public PageList(int pageSize) {
        setPageSize(pageSize);
    }

    public PageList(int pageNo, int pageSize) {
        setPageNo(pageNo);
        setPageSize(pageSize);
    }

    public PageList(int pageNo, int pageSize, long totalCount, List<T> list) {
        setPageNo(pageNo);
        setPageSize(pageSize);
        setTotalCount(totalCount);
        setList(list);
    }

    public PageList(PageParam pageParam, long totalCount, List<T> list) {
        setPageNo(pageParam.getPageNo());
        setPageSize(pageParam.getPageSize());
        setTotalCount(totalCount);
        setList(list);
    }

    public void addExtra(String key, Object value) {
        if (extra == null) {
            extra = new HashMap<>();
        }

        extra.put(key, value);
    }

    public <E> PageList<E> convertListData(Function<List<T>, List<E>> converter) {
        return new PageList(this.pageNo, this.pageSize, this.totalCount, converter.apply(this.list));
    }

    public static <T> PageList<T> emptyPage(PageParam pageParam) {
        return PageList.<T>builder()
                .pageNo(pageParam.getPageNo()).pageSize(pageParam.getPageSize())
                .totalCount(0).list(Collections.emptyList()).build();
    }

    public static <T> PageList<T> memoryPage(List<T> list, PageParam pageParam) {
        if (list == null || list.size() == 0) {
            return emptyPage(pageParam);
        }

        return PageList.<T>builder()
                .pageNo(pageParam.getPageNo()).pageSize(pageParam.getPageSize())
                .totalCount(list.size()).list(pageParam.getSubList(list)).build();
    }

    /**
     * 设置当前页的页号,序号从1开始,低于1时自动调整为1.
     */
    public void setPageNo(int pageNo) {
        this.pageNo = (pageNo < 1 ? 1 : pageNo);
    }

    /**
     * 返回Page对象自身的setPageNo函数,可用于连续设置。
     */
    public PageList<T> pageNo(int thePageNo) {
        setPageNo(thePageNo);
        return this;
    }

    /**
     * 设置每页的记录数量.
     */
    public void setPageSize(int pageSize) {
        this.pageSize = (pageSize <= 0 ? 10 : pageSize);
    }

    /**
     * 返回Page对象自身的setPageSize函数,可用于连续设置。
     */
    public PageList<T> pageSize(int thePageSize) {
        setPageSize(thePageSize);
        return this;
    }

    /**
     * 根据pageNo和pageSize计算当前页第一条记录在总结果集中的位置,序号从1开始.
     */
    public int getFirst() {
        return ((pageNo - 1) * pageSize) + 1;
    }

    /**
     * 返回Page对象自身的setOrderBy函数,可用于连续设置。
     */
    public PageList<T> orderBy(String theOrderBy) {
        setOrderBy(theOrderBy);

        return this;
    }

    /**
     * 是否已设置排序字段,无默认值.
     */
    public boolean hasOrderBy() {
        return (orderBy != null && !"".equals(orderBy));
    }

    /**
     * 设置页内的记录列表.
     */
    public void setList(List<T> list) {
        if (list == null || list.size() == 0) {
            return;
        }

        this.list = list;
    }

    /**
     * 根据pageSize与totalCount计算总页数, 默认值为-1.
     */
    public long getTotalPages() {
        if (totalCount < 0) {
            return -1;
        }

        long count = totalCount / pageSize;
        if (totalCount % pageSize > 0) {
            count++;
        }

        return count;
    }

    /**
     * 是否还有下一页.
     */
    public boolean hasNext() {
        return (pageNo + 1 <= getTotalPages());
    }

    /**
     * 取得下页的页号, 序号从1开始. 当前页为尾页时仍返回尾页序号.
     */
    public int getNextPage() {
        return (hasNext() ? pageNo + 1 : pageNo);
    }

    /**
     * 是否还有上一页.
     */
    public boolean hasPre() {
        return (pageNo - 1 >= 1);
    }

    /**
     * 取得上页的页号, 序号从1开始. 当前页为首页时返回首页序号.
     */
    public int getPrePage() {
        return (hasPre() ? pageNo - 1 : pageNo);
    }

}