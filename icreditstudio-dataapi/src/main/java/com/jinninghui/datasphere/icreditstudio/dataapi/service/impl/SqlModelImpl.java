package com.jinninghui.datasphere.icreditstudio.dataapi.service.impl;

import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditGenerateApiEntity;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.ApiDetailResult;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.result.GenerateApiResult;
import com.jinninghui.datasphere.icreditstudio.framework.result.util.BeanCopyUtils;
import org.springframework.stereotype.Service;

/**
 * @author xujie
 * @description sql生成模式
 * @create 2022-02-24 14:46
 **/
@Service
public final class SqlModelImpl extends ApiBaseGenerateService {

    @Override
    public void setApiBaseResult(ApiDetailResult result) {
        super.setApiBaseResult(result);
    }

    public void singleTableCreateModel(ApiDetailResult result, IcreditGenerateApiEntity generateApiEntity, String databaseName) {
        //单表生成模式
        GenerateApiResult generateApiResult = new GenerateApiResult();
        BeanCopyUtils.copyProperties(generateApiEntity, generateApiResult);
        generateApiResult.setDatabaseName(databaseName);
        result.setGenerateApi(generateApiResult);
    }

    public void sqlCreateModel(ApiDetailResult result, IcreditGenerateApiEntity generateApiEntity, String databaseName) {
        //sql生成模式
        GenerateApiResult generateApiResult = new GenerateApiResult();
        BeanCopyUtils.copyProperties(generateApiEntity, generateApiResult);
        generateApiResult.setDatabaseName(databaseName);
        result.setGenerateApi(generateApiResult);
    }

    public void chainCreateModel(ApiDetailResult result, IcreditGenerateApiEntity generateApiEntity, String databaseName) {
        //链上生成模式
        GenerateApiResult generateApiResult = new GenerateApiResult();
        BeanCopyUtils.copyProperties(generateApiEntity, generateApiResult);
        generateApiResult.setDatabaseName(databaseName);
        result.setGenerateApi(generateApiResult);
    }
}
