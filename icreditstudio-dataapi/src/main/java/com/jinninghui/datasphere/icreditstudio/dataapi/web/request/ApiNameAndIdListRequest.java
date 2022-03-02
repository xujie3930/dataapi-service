package com.jinninghui.datasphere.icreditstudio.dataapi.web.request;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ApiNameAndIdListRequest {

    private List<String> apiGroupIds;

}
