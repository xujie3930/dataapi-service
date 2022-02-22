package com.jinninghui.datasphere.icreditstudio.dataapi.web.request;

import com.jinninghui.datasphere.icreditstudio.framework.result.base.BusinessBasePageForm;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author xujie
 * @description API列表查询参数
 * @create 2022-02-22 15:39
 **/
@Data
public class ApiBaseListRequest  extends BusinessBasePageForm {
    //API分组id
    @NotBlank(message = "API分组id不能为空")
    private String apiGroupId;
    //API名称
    private String name;
    //API Path
    private String path;
    //发布状态：0-待发布，1-未发布，2-已发布
    private Integer publishStatus;
    //API类型：0-注册API，1-数据源生成API
    private Integer type;
    //发布开始时间
    private String publishTimeStart;
    //发布结束时间
    private String publishTimeEnd;
}
