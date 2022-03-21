package com.jinninghui.datasphere.icreditstudio.dataapi.web.request;

import com.jinninghui.datasphere.icreditstudio.framework.result.base.BusinessBasePageForm;
import lombok.Data;

@Data
public class ApiHistoryListRequest extends BusinessBasePageForm {

    private String apiId;
    private String publishUser;
    private Integer apiVersion;
    private Integer pageStartNum;

}
