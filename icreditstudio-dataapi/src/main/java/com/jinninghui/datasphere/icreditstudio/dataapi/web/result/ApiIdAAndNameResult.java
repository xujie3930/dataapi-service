package com.jinninghui.datasphere.icreditstudio.dataapi.web.result;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiIdAAndNameResult {

    private String id;
    private String name;
    private Boolean leaf;
    private Integer level;
    private String parentId;
    private String grandParentId;

}
