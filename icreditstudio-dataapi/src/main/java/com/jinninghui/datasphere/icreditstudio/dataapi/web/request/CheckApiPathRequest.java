package com.jinninghui.datasphere.icreditstudio.dataapi.web.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckApiPathRequest {

    private String id;
    private String path;

}
