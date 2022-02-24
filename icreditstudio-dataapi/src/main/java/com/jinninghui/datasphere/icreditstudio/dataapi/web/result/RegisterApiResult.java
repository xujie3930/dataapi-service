package com.jinninghui.datasphere.icreditstudio.dataapi.web.result;

import lombok.Data;

/**
 * @author xujie
 * @description 注册API返回参数
 * @create 2022-02-24 10:43
 **/
@Data
public class RegisterApiResult {
    //后台服务Hots,注册API独有
    private String host;
    //后台服务,注册API独有
    private String path;
}
