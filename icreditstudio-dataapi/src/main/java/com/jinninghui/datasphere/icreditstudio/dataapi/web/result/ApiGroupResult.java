package com.jinninghui.datasphere.icreditstudio.dataapi.web.result;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author xujie
 * @description API分组返回参数
 * @create 2022-02-22 14:07
 **/
@Data
@AllArgsConstructor
public class ApiGroupResult {
    private String apiGroupName;
    private String apiGroupId;
}
