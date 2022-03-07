package com.jinninghui.datasphere.icreditstudio.dataapi.service.impl;

import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditGenerateApiEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.enums.ApiModelTypeEnum;
import com.jinninghui.datasphere.icreditstudio.dataapi.feign.DatasourceFeignClient;
import com.jinninghui.datasphere.icreditstudio.dataapi.feign.result.DataSourceInfoRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.feign.result.DatasourceDetailResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.feign.vo.ConnectionInfoVO;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.ApiBaseService;
import com.jinninghui.datasphere.icreditstudio.dataapi.service.IcreditGenerateApiService;
import com.jinninghui.datasphere.icreditstudio.dataapi.utils.DatasourceUtils;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.ApiDetailResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.BusinessResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author xujie
 * @description 数据源生成api
 * @create 2022-02-24 14:35
 **/
@Service
public class ApiBaseGenerateService implements ApiBaseService {
    @Autowired
    private IcreditGenerateApiService generateApiService;
    @Autowired
    private DatasourceFeignClient dataSourceFeignClient;
    @Autowired
    private SqlModelImpl sqlModel;

    @Override
    public void setApiBaseResult(ApiDetailResult result) {
        IcreditGenerateApiEntity generateApiEntity = generateApiService.getByApiBaseId(result.getId());
        if (Objects.isNull(generateApiEntity)) {
            return;
        }
        BusinessResult<DatasourceDetailResult> connResult = dataSourceFeignClient.info(generateApiEntity.getDatasourceId());
        String databaseName = "";
        if (connResult.isSuccess() && !Objects.isNull(connResult.getData())) {
            databaseName = connResult.getData().getName();
        }
        ApiModelTypeEnum apiModelTypeEnum = ApiModelTypeEnum.findByModel(generateApiEntity.getModel());
        switch (apiModelTypeEnum) {
            case SINGLE_TABLE_CREATE_MODEL:
                sqlModel.singleTableCreateModel(result, generateApiEntity, databaseName);
                break;
            case SQL_CREATE_MODEL:
                break;
            case CHAIN_CREATE_MODEL:
                break;
            default:
                break;
        }
    }
}
