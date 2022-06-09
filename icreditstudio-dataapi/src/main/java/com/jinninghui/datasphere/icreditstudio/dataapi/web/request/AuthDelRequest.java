package com.jinninghui.datasphere.icreditstudio.dataapi.web.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 删除授权API,保存参数
 * @author  maoc
 * @create  2022/6/2 10:46
 * @desc
 **/
public class AuthDelRequest {
    //应用主键id
    @NotBlank(message = "20000021")
    private String appId;
    //auth的主键id
    @NotEmpty
    private List<String> authList;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public List<String> getAuthList() {
        return authList;
    }

    public void setAuthList(List<String> authList) {
        this.authList = authList;
    }
}
