package com.jinninghui.datasphere.icreditstudio.dataapi.dto;

import lombok.Data;

@Data
public class RegisterApiDTO {

    private Integer apiVersion;
    private String apiName;
    private String apiDesc;
    private String reqHost;
    private String reqPath;

}
