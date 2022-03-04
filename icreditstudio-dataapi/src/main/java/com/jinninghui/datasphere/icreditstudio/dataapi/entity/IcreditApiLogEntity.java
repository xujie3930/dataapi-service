package com.jinninghui.datasphere.icreditstudio.dataapi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jinninghui.datasphere.icreditstudio.framework.result.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.htrace.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

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
public class IcreditApiLogEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ID_WORKER_STR)
    private String id;

    private String apiName;

    private String apiId;

    private String apiPath;

    private String appName;

    private String appId;

    private String callIp;

    private Integer apiVersion;

    private String requestProtocol;

    private String requestType;

    private String responseType;

    private String requestParam;

    private String responseParam;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date callBeginTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date callEndTime;

    private Long runTime;

    private Integer callStatus;

    private Integer apiType;
    private String exceptionDetail;
    //具体执行sql
    private String executeSql;
    private String traceId;

}
