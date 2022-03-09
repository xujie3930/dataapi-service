package com.jinninghui.datasphere.icreditstudio.dataapi.service.bo;

import com.jinninghui.datasphere.icreditstudio.dataapi.entity.IcreditApiParamEntity;
import lombok.Data;

import java.util.List;

@Data
public class SqlModelInfoBO {

    private List<IcreditApiParamEntity> apiParamEntityList;
    private String tableNames;

}
