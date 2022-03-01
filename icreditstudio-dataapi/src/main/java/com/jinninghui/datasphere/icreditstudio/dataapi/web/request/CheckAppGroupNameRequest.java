package com.jinninghui.datasphere.icreditstudio.dataapi.web.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckAppGroupNameRequest {
    @NotBlank(message = "应用分组id不能为空")
    private String id;
    private String name;
}
