package com.jinninghui.datasphere.icreditstudio.dataapi.web.result;

import lombok.Data;

@Data
public class ApiGroupIdAAndNameResult {

    private String id;
    private String name;
    private ApiIdAAndNameResult children;
    private Boolean leaf = false;

}
