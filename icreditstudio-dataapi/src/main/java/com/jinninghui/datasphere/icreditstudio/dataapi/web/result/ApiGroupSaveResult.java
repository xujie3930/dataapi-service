package com.jinninghui.datasphere.icreditstudio.dataapi.web.result;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author xujie
 * @description 保存API分组返回参数
 * @create 2022-02-23 14:38
 **/
@Data
@AllArgsConstructor
public class ApiGroupSaveResult {
    private String workId;
    private String apiGroupId;
}
