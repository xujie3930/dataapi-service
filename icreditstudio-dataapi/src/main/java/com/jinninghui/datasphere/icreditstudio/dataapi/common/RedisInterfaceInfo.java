package com.jinninghui.datasphere.icreditstudio.dataapi.common;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class RedisInterfaceInfo {

    private String url;
    private String userName;
    private String password;
    private String requiredFields;
    private String querySql;
    private List<TokenInfo> tokenList = new LinkedList<>();
}
