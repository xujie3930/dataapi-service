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
    private String apiVersion;

    //请求协议?
    private String requestProtocol;

    //GET
    private String requestType;

    //JSON
    private String responseType;

    //可以获取
    private String requestParam;

    //可以获取
    private String responsePatam;

    //可以获取
    private String callBeginTime;

    //可以获取
    private String callEndTime;

    //可以获取
    private String runTime;

    //(请求中,成功,失败)
    private String callStatusStr;

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


}
