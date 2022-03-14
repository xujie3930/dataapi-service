package com.jinninghui.datasphere.icreditstudio.dataapi.web.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class WorkFlowRenameRequest {

    @NotBlank(message = "20000029")
    private String id;
    private String newName;

}
