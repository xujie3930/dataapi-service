package com.jinninghui.datasphere.icreditstudio.dataapi.web.request;

import lombok.Data;

@Data
public class AuthInfoRequest {

    private String appId;
    private Integer publishStatus;
    private String apiId;
}
