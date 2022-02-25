package com.jinninghui.datasphere.icreditstudio.dataapi.service;

import com.jinninghui.datasphere.icreditstudio.dataapi.common.FieldInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditApiBaseEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.param.DatasourceApiSaveParam;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.*;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.ApiSaveResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessPageResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * API表基础信息表 服务类
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
public interface IcreditApiBaseService extends IService<IcreditApiBaseEntity> {

    BusinessResult<BusinessPageResult> getList(ApiBaseListRequest request);

    BusinessResult<ApiSaveResult> createDataSourceApi(String userId, DatasourceApiSaveParam param);

    BusinessResult<List<Map<String, Object>>> getDataSourcesList(DataSourcesListRequest request);

    BusinessResult<List<Map<String, String>>> getTableNameList(TableNameListRequest request);

    BusinessResult<List<FieldInfo>> getTableFieldList(TableFieldListRequest request);

    BusinessResult<Boolean> checkApiPath(CheckApiPathRequest request);

    BusinessResult<Boolean> checkApiName(CheckApiNameRequest request);

    BusinessResult<Boolean> checkQuerySql(CheckQuerySqlRequest request);
}
