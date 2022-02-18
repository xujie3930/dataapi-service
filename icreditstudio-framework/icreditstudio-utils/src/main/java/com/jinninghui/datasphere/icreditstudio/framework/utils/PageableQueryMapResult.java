package com.jinninghui.datasphere.icreditstudio.framework.utils;

import java.util.List;
import java.util.Map;

/**
 * @author King
 */
public class PageableQueryMapResult extends PageableQueryResult<Map<String, Object>> {
    private static final long serialVersionUID = -2503880293474633503L;
    public final static String CODE = "ReturnCode";
    public final static String ROWS = "rows";
    public final static String TOTAL_ROWS = "TotalRows";
    public final static String EFFECT_ROWS = "EffectRows";

    public PageableQueryMapResult(List<Map<String, Object>> rows, int totalCount) {
        super(rows, totalCount);
    }

    public PageableQueryMapResult(Data result) {
        this(result.getRows(ROWS), result.getInt(TOTAL_ROWS, -1));
    }

    public Data getData(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < this.rowsCount) {
            return new Data(this.rows.get(rowIndex));
        } else {
            return null;
        }
    }
}
