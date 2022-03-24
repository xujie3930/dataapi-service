package com.jinninghui.datasphere.icreditstudio.dataapi.web.request;

import lombok.Data;

@Data
public class ApiGroupRenameRequest {

    private String id;
    private String newName;
    private String desc;

}
