package com.jinninghui.datasphere.icreditstudio.dataapi.web.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckAppGroupNameRequest {
    @NotBlank(message = "20000020")
    private String id;
    private String name;
}
