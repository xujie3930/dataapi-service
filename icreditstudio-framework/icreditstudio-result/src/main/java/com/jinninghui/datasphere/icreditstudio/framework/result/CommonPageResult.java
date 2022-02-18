package com.jinninghui.datasphere.icreditstudio.framework.result;

import java.util.List;
import java.util.Map;

/**
 *  
 * @author jidonglin
 * @author liyanhui
 *
 * @param <T>
 */
@Deprecated
public class CommonPageResult<T> extends CommonOuterResponse {

    /**
     * 总记录数
     */
    private long total;

    /**
     * 总页数
     */
    private Integer pageCount;
    /**
     * 当前页号
     */
    private Integer pageNum;
    /**
     * 页面大小
     */
    private Integer pageSize;

    /**
     * 数据列表
     */
    private List<T> rows;
    

    /**
     * 需要统计字段， key为字段名，value为显示的中文名。字段必须是数值
     */
    private List<Map<String, String>> statistics;
    
    private List<String> titles;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public List<Map<String, String>> getStatistics() {
        return statistics;
    }

    public void setStatistics(List<Map<String, String>> statistics) {
        this.statistics = statistics;
    }

	public List<String> getTitles() {
		return titles;
	}

	public void setTitles(List<String> titles) {
		this.titles = titles;
	}

    public Integer getPageCount() {
        return pageCount;
    }

    public void setPageCount(Integer pageCount) {
        this.pageCount = pageCount;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}