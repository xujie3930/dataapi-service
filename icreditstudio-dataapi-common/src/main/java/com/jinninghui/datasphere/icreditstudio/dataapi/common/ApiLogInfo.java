package com.jinninghui.datasphere.icreditstudio.dataapi.common;

import lombok.Data;
import lombok.EqualsAndHashCode;

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
public class ApiLogInfo implements Serializable {

    //需要插入redis?
    private String apiName;

    private Integer apiType;

    //可以获取
    private String apiId;

    //通过接口获取
    private String apiPath;

    //可以获取
    private String appName;

    //可以获取
    private String appId;

    //可以获取
    private String callIp;

    //可以获取
    private Integer apiVersion;

    //请求协议
    private String requestProtocol = "HTTP";

    //GET
    private String requestType = "GET";

    //JSON
    private String responseType = "JSON";

    //可以获取
    private String requestParam;

    //可以获取
    private String responseParam;

    //可以获取
    private Date callBeginTime;

    //可以获取
    private Date callEndTime;

    //可以获取
    private Long runTime;

    //(0请求中,1成功,2失败)
    private Integer callStatus;

    private String remark;

    //接口调用时间
    private Date createTime;

    //调用方UserId
    private String createBy;

    //接口调用时间
    private Date updateTime;

    //调用方UserId
    private String updateBy;

    private Integer delFlag = 0;

    //kafka唯一id
    private String traceId;

    //具体执行sql
    private String executeSql;

    //异常日志
    private String exceptionDetail;

}
