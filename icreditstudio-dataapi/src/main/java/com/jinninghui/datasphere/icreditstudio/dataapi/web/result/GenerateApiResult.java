package com.jinninghui.datasphere.icreditstudio.dataapi.web.result;

import lombok.Data;

/**
 * @author xujie
 * @description 数据源生成API返回参数
 * @create 2022-02-24 10:45
 **/
@Data
public class GenerateApiResult {
    //API模式,数据源生成API方式独有字段
    private Integer model;
    //数据源名称，数据源生成API方式独有字段
    private String databaseName;
    private String datasourceId;
    //数据表名称，数据源生成API方式独有字段
    private String tableName;
    private String sql;
}
