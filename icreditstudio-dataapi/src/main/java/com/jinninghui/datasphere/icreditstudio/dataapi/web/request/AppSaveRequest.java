package com.jinninghui.datasphere.icreditstudio.dataapi.web.request;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

/**
 * @author xujie
 * @description APP应用分组新增参数
 * @create 2022-02-24 16:40
 **/
@Data
public class AppSaveRequest {
    private String id;
    //认证方式(0-密钥,1-证书认证)，默认0
    private Integer certificationType;
    //是否启用(0-未启用,1-启用),默认1
    private Integer isEnable;
    //应用名称
    @NotBlank(message = "20000022")
    @Length(max = 50, message = "20000023")
    private String name;
    //密钥/RSA公钥
    private String secretContent;
    //应用分组ID
    @NotBlank(message = "20000024")
    private String appGroupId;
    //备注
    private String desc;

    //tokenType:0-长期，1-8小时，2-自定义
    private Integer tokenType;
    //token有效期
    private Integer period;
    //允许访问ip
    private String allowIp;
    //应用id
    private String generateId;
}
