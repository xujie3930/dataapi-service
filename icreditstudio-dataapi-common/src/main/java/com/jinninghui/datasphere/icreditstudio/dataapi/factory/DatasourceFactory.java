package com.jinninghui.datasphere.icreditstudio.dataapi.factory;

import com.jinninghui.datasphere.icreditstudio.dataapi.common.DatasourceTypeEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.factory.impl.MysqlDatasource;
import com.jinninghui.datasphere.icreditstudio.dataapi.factory.impl.OracleDatasource;
import com.jinninghui.datasphere.icreditstudio.dataapi.factory.impl.PostgreDatasource;

/**
 * @author xujie
 * @description 数据源工厂
 * @create 2021-08-25 15:32
 **/
public class DatasourceFactory {

    public static DatasourceSync getDatasource(Integer type) {
        DatasourceTypeEnum datasourceTypeEnum = DatasourceTypeEnum.findDatasourceTypeByType(type);
        switch (datasourceTypeEnum) {
            case MYSQL:
                return new MysqlDatasource();
            case ORACLE:
                return new OracleDatasource();
            case  POSTGRESQL:
                return new PostgreDatasource();
            default:
                return new MysqlDatasource();
        }
    }
}
