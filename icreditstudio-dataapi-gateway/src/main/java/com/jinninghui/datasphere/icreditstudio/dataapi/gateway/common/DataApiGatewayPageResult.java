package com.jinninghui.datasphere.icreditstudio.dataapi.gateway.common;

import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessPageInfo;
import com.jinninghui.datasphere.icreditstudio.framework.result.base.BusinessBasePageForm;

import java.util.List;

/**
 * @author liyanhui
 */
public class DataApiGatewayPageResult<T> implements java.io.Serializable {
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

    private DataApiGatewayPageResult(){}

    public DataApiGatewayPageResult(long total, long pageNum, long pageSize, List<T> list) {
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.list = list;
        this.pageCount = (total % pageSize > 0) ? (total / pageSize + 1) : (total / pageSize);
    }

    @Deprecated
    public DataApiGatewayPageResult(List<T> list, BusinessPageInfo pageQueryRequest, long total){
        DataApiGatewayPageResult result = build(list, pageQueryRequest, total);
        this.total = result.getTotal();
        this.pageCount = result.getPageCount();
        this.pageNum = result.getPageNum();
        this.pageSize = result.getPageSize();
        this.list = list;
    }

    @Deprecated
    public static <T> DataApiGatewayPageResult<T> build(List<T> list, BusinessPageInfo pageQueryRequest, long total) {

        int pageSize = pageQueryRequest.getPageSize();
        int pageNum = pageQueryRequest.getPageNum();
        if (total < 0 || pageSize <= 0 || pageNum < 0) {
            throw new IllegalArgumentException("total must more than 0");
        }

        DataApiGatewayPageResult<T> result = new DataApiGatewayPageResult<>();

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

    public DataApiGatewayPageResult(List<T> list, BusinessBasePageForm pageQueryRequest, long total){
        DataApiGatewayPageResult result = build(list, pageQueryRequest, total);
        this.total = result.getTotal();
        this.pageCount = result.getPageCount();
        this.pageNum = result.getPageNum();
        this.pageSize = result.getPageSize();
        this.list = list;
    }

    public static <T> DataApiGatewayPageResult<T> build(List<T> list, BusinessBasePageForm pageQueryRequest, long total) {

        int pageSize = pageQueryRequest.getPageSize();
        int pageNum = pageQueryRequest.getPageNum();
        if (total < 0 || pageSize <= 0 || pageNum < 0) {
            throw new IllegalArgumentException("total must more than 0");
        }

        DataApiGatewayPageResult<T> result = new DataApiGatewayPageResult<>();

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
