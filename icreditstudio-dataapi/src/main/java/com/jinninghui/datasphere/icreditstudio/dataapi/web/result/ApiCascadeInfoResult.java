package com.jinninghui.datasphere.icreditstudio.dataapi.web.result;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ApiCascadeInfoResult {

    private String id;
    private String name;
    private List<ApiGroupIdAAndNameResult> children;
    private Boolean leaf;

}
