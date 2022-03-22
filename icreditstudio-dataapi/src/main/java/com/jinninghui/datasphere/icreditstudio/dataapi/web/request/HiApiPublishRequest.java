package com.jinninghui.datasphere.icreditstudio.dataapi.web.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HiApiPublishRequest {

    private String apiHiId;
    private Integer publishStatus;//1--停止发布，2--发布

}
