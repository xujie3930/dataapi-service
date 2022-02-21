package com.jinninghui.datasphere.icreditstudio.dataapi.web.request;

import lombok.Data;

/**
 * @author xujie
 * @description API分组保存参数
 * @create 2022-02-21 16:23
 **/
@Data
public class ApiGroupSaveRequest {
    //业务流程id
    private String workId;
    //api分组名称
    private String name;
    //api分组描述
    private String desc;
}
