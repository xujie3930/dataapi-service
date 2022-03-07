package com.jinninghui.datasphere.icreditstudio.dataapi.web.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class AppDetailRequest {
    @NotBlank(message = "20000021")
    private String id;
}
