package com.jinninghui.datasphere.icreditstudio.dataapi.web.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiPublishRequest {

    private String id;
    private Integer publishStatus;//1--停止发布，2--发布

}
