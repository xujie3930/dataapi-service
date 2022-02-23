package com.jinninghui.datasphere.icreditstudio.dataapi.web.result;

import com.jinninghui.datasphere.icreditstudio.framework.result.base.BaseEntity;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * API表基础信息返回参数
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
@Data
public class ApiBaseResult implements Serializable {

    private String id;

    private Integer type;

    private String name;

    private String path;

    private String apiGroupId;

    private Integer apiVersion;

    private Integer publishStatus;

    private String publishUser;

    private Long publishTime;

    private Integer delFlag;
}
