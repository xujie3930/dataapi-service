package com.jinninghui.datasphere.icreditstudio.framework.result;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jinninghui.datasphere.icreditstudio.framework.result.base.BusinessBasePageForm;

import java.util.List;

/**
 * @author liyanhui
 */
public class BusinessPageResult<T> implements java.io.Serializable {
    private static final long serialVersionUID = -3628865867907230918L;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 总页数
     */
    private long pageCount;
    /**
     * 当前页号
     */
    private long pageNum;
    /**
     * 页面大小
     */
    private long pageSize;

    /**
     * 数据列表
     */
    private List<T> list;

    private BusinessPageResult(){}

    public BusinessPageResult(long total, long pageNum, long pageSize, List<T> list) {
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.list = list;
        this.pageCount = (total % pageSize > 0) ? (total / pageSize + 1) : (total / pageSize);
    }

    @Deprecated
    public BusinessPageResult(List<T> list, BusinessPageInfo pageQueryRequest, long total){
        BusinessPageResult result = build(list, pageQueryRequest, total);
        this.total = result.getTotal();
        this.pageCount = result.getPageCount();
        this.pageNum = result.getPageNum();
        this.pageSize = result.getPageSize();
        this.list = list;
    }

    @Deprecated
    public static <T> BusinessPageResult<T> build(List<T> list, BusinessPageInfo pageQueryRequest, long total) {

        int pageSize = pageQueryRequest.getPageSize();
        int pageNum = pageQueryRequest.getPageNum();
        if (total < 0 || pageSize <= 0 || pageNum < 0) {
            throw new IllegalArgumentException("total must more than 0");
        }

        BusinessPageResult<T> result = new BusinessPageResult<>();

        result.setList(list);
        result.setTotal(total);

        result.setPageSize(pageSize);
        result.setPageNum(pageNum);

        if (total == 0) {
            result.setPageCount(0);
        } else {
            if (total % pageSize > 0) {
                result.setPageCount(total / pageSize + 1);
            } else {
                result.setPageCount(total / pageSize);
            }
        }

        return result;
    }

    public BusinessPageResult(List<T> list, BusinessBasePageForm pageQueryRequest, long total){
        BusinessPageResult result = build(list, pageQueryRequest, total);
        this.total = result.getTotal();
        this.pageCount = result.getPageCount();
        this.pageNum = result.getPageNum();
        this.pageSize = result.getPageSize();
        this.list = list;
    }

    public static <T> BusinessPageResult<T> build(List<T> list, BusinessBasePageForm pageQueryRequest, long total) {

        int pageSize = pageQueryRequest.getPageSize();
        int pageNum = pageQueryRequest.getPageNum();
        if (total < 0 || pageSize <= 0 || pageNum < 0) {
            throw new IllegalArgumentException("total must more than 0");
        }

        BusinessPageResult<T> result = new BusinessPageResult<>();

        result.setList(list);
        result.setTotal(total);

        result.setPageSize(pageSize);
        result.setPageNum(pageNum);

        if (total == 0) {
            result.setPageCount(0);
        } else {
            if (total % pageSize > 0) {
                result.setPageCount(total / pageSize + 1);
            } else {
                result.setPageCount(total / pageSize);
            }
        }

        return result;
    }

    public static <T> BusinessPageResult<T> build(IPage<T> page, BusinessBasePageForm pageQueryRequest) {

        int pageSize = pageQueryRequest.getPageSize();
        int pageNum = pageQueryRequest.getPageNum();
        long total = page.getTotal();
        if (total < 0 || pageSize <= 0 || pageNum < 0) {
            throw new IllegalArgumentException("total must more than 0");
        }

        BusinessPageResult<T> result = new BusinessPageResult<>();

        result.setList(page.getRecords());
        result.setTotal(total);

        result.setPageSize(pageSize);
        result.setPageNum(pageNum);

        if (total == 0) {
            result.setPageCount(0);
        } else {
            if (total % pageSize > 0) {
                result.setPageCount(total / pageSize + 1);
            } else {
                result.setPageCount(total / pageSize);
            }
        }

        return result;
    }


    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public long getPageCount() {
        return pageCount;
    }

    public void setPageCount(long pageCount) {
        this.pageCount = pageCount;
    }

    public long getPageNum() {
        return pageNum;
    }

    public void setPageNum(long pageNum) {
        this.pageNum = pageNum;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }


}
