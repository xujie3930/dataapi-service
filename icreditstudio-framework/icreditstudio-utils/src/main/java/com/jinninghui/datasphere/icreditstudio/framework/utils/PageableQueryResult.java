package com.jinninghui.datasphere.icreditstudio.framework.utils;

import java.io.Serializable;
import java.util.List;

public class PageableQueryResult<T> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2064434139299876871L;
	protected final List<T> rows;
	protected final int totalCount;
	protected final int rowsCount;
	
	public PageableQueryResult(List<T> rows, int totalCount) {
		this.rows = rows;
		this.totalCount = totalCount;
		this.rowsCount = rows.size();
	}

	public List<T> getRows() {
		return rows;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public int getRowsCount() {
		return rowsCount;
	}
	
}
