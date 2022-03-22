package com.jinninghui.datasphere.icreditstudio.dataapi.web.result;

import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.RegisterRequestParamSaveRequest;
import com.jinninghui.datasphere.icreditstudio.dataapi.web.request.RegisterResponseParamSaveRequest;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

/**
 * @author xujie
 * @description api详情返回参数
 * @create 2022-02-23 16:53
 **/
@Data
public class ApiDetailResult {
    //API主键id
    private String id;
    //API类型：0-注册API，1-数据源生成API
    private Integer type;
    //API名称
    private String name;
    //API PATH
    private String apiPath;
    //API名称
    private String reqPath;
    //API PATH
    private String reqHost;
    //接口地址
    private String interfaceAddress;
    //协议
    private String protocol;
    //请求方式
    private String requestType;
    //返回类型
    private String responseType;
    //目标文件夹（业务流程名称/API分组名称）
    private String destination;
    //发布状态：0-待发布，1-未发布，2-已发布
    private Integer publishStatus;
    //API版本号
    private Integer apiVersion;
    //创建人
    private String createBy;
    //创建时间
    private Long createTime;
    //发布人
    private String publishUser;
    //发布时间
    private Long publishTime;
    //备注
    private String desc;
    //选择参数列表
    private List<APIParamResult> paramList = new LinkedList<>();
    //注册api的请求参数
    private List<RegisterRequestParamSaveRequest> registerRequestParamSaveRequestList;
    //注册api的返回参数
    private List<RegisterResponseParamSaveRequest> registerResponseParamSaveRequestList;
    //注册API参数
    private RegisterApiResult registerApi;
    //数据源生成API,sql生成模式
    private GenerateApiResult generateApi;

    private String apiHiId;//历史版本api主键id（历史列表编辑用）
    private String workFlowName;//业务流程名称（历史列表编辑用）
    private String workFlowId;//业务流程id（历史列表编辑用）
    private String apiGroupName;//api分组名称（历史列表编辑用）
    private String apiGroupId;//api分组id（历史列表编辑用）
}
