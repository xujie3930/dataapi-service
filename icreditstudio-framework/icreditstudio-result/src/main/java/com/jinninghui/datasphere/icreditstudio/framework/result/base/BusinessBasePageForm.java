package com.jinninghui.datasphere.icreditstudio.framework.result.base;

/**
 * @author liyanhui
 */
public class BusinessBasePageForm extends BaseRequest {
    private static final long serialVersionUID = -4212911809130413023L;

    /**
     * 页数
     */
    private int pageNum = 1;

    /**
     * 一页显示数量
     */
    private int pageSize = 10;

    /**
     * 用来排序的字段
     */
    private String orderBy;

    /**
     * 排序顺序 DESC & ASC
     */
    private String order;
    private int pageStart = 0;

    public int getPageNum() {
        return this.pageNum;
    }

    public void setPageNum(int pageNum) {
        if (pageNum >= 1) {
            this.pageNum = pageNum;
            this.pageStart = (this.pageNum-1)*this.pageSize;
        }
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public void setPageSize(int pageSize) {
        if (pageSize >= 1) {
            this.pageSize = pageSize;
            this.pageStart = (this.pageNum-1)*this.pageSize;
        }
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public int getPageStart() {
        return pageStart;
    }
}