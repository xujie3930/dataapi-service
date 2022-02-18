package com.jinninghui.datasphere.icreditstudio.framework.result;

@Deprecated
public class BusinessPageInfo {

    public BusinessPageInfo(int currentPage, int pageSize) {
       this(currentPage, pageSize, null, null);
    }

    public BusinessPageInfo(int currentPage, int pageSize, String orderBy) {
        this(currentPage, pageSize, orderBy, "DESC");
    }

    public BusinessPageInfo(int currentPage, int pageSize, String orderBy, String order){
        if (currentPage < 1 || pageSize < 1) {
            throw new IllegalArgumentException("currentPage and pageSize must more than 0.");
        }

        this.pageNum = currentPage;
        this.pageSize = pageSize;
        this.order = order;
        this.orderBy = orderBy;
    }

    /**
     * 当前页号
     */
    private int pageNum;
    /**
     * 页面大小
     */
    private int pageSize;

    /**
     * 用来排序的字段
     */
    private String orderBy;

    /**
     * 排序顺序 DESC & ASC
     */
    private String order;

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getOrderBy() {
        return orderBy + " " + order;
    }

    /**
     * 计算查询的开始行数
     * @return
     */
    public int getStartRow(){
        return (pageNum - 1) * pageSize;
    }
}
