package com.jinninghui.datasphere.icreditstudio.dataapi.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 调用日志
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("icredit_api_log")
public class IcreditApiLogEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String apiName;

    private String apiId;

    private String apiPath;

    private String appName;

    private String appId;

    private String callIp;

    private String apiVersion;

    private String requestProtocol;

    private String requestType;

    private String responseType;

    private String requestParam;

    private String responsePatam;

    private String callBeginTime;

    private String callEndTime;

    private String runTime;

    private String callStatusStr;

    private Boolean callStatus;

    private String remark;

    private LocalDateTime createTime;

    private String createBy;

    private LocalDateTime updateTime;

    private String updateBy;

    private Boolean delFlag;


}
