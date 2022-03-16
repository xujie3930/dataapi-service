package com.jinninghui.datasphere.icreditstudio.dataapi.service.bo;

import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditApiParamEntity;
import lombok.Data;

import java.util.List;

@Data
public class CreateApiInfoBO {

    private List<IcreditApiParamEntity> apiParamEntityList;
    private String tableNames;
    private String requiredFieldStr;
    private String responseFieldStr;
    private String querySql;

}
