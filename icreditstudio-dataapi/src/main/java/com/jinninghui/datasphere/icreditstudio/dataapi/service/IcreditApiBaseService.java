package com.jinninghui.datasphere.icreditstudio.dataapi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.FieldInfo;
import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditApiBaseEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.param.DatasourceApiSaveParam;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.ApiBaseListRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.DataSourcesListRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.TableFieldListRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.TableNameListRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.ApiDetailResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.*;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.ApiNameAndIdListResult;
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

    BusinessResult<ApiDetailResult> detail(String id);

    BusinessResult<Boolean> checkApiPath(CheckApiPathRequest request);

    BusinessResult<Boolean> checkApiName(CheckApiNameRequest request);

    Object checkQuerySql(CheckQuerySqlRequest request, String id, Integer apiVersion, Integer type);

    BusinessResult<Boolean> publishOrStop(String userId, ApiPublishRequest request);

    BusinessResult<List<ApiNameAndIdListResult>> getApiByApiGroupId(ApiNameAndIdListRequest request);

    String findPublishedByWorkFlowId(String id);

    List<String> getIdsByApiGroupIds(List<String> apiGroupIdList);

    String findPublishedByApiGroupId(String id);
}
