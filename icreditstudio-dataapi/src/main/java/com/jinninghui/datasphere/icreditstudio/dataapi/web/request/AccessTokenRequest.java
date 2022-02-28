package com.jinninghui.datasphere.icreditstudio.dataapi.web.request;

import lombok.Data;

/**
 * @author xujie
 * @description 根据应用标识，获取token
 * @create 2021-12-06 15:32
 **/
@Data
public class AccessTokenRequest{
    private String appFlag;
}
