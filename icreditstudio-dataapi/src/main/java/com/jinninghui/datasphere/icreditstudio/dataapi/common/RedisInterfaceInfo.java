package com.jinninghui.datasphere.icreditstudio.dataapi.common;

import lombok.Data;

@Data
public class RedisInterfaceInfo {

    private String url;
    private String userName;
    private String password;
    private String requiredFields;
    private String querySql;

}
