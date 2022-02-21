package com.jinninghui.datasphere.icreditstudio.dataapi.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * API表基础信息表
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("icredit_api_base")
public class IcreditApiBaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean type;

    private String name;

    private String path;

    private String requestType;

    private String responseType;

    private String desc;

    private String apiGroupId;

    private Integer apiVersion;

    private Boolean publishStatus;

    private String publishUser;

    private LocalDateTime publishTime;

    private String remark;

    private LocalDateTime createTime;

    private String createBy;

    private LocalDateTime updateTime;

    private String updateBy;

    private Boolean delFlag;


}
