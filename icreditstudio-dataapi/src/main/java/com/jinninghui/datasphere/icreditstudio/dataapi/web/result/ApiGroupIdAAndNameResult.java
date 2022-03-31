package com.jinninghui.datasphere.icreditstudio.dataapi.web.result;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ApiGroupIdAAndNameResult {

    private String id;
    private String name;
    private List<ApiIdAAndNameResult> children;
    private Boolean leaf;
    private Integer level;
    private String parentId;

}
