package com.jinninghui.datasphere.icreditstudio.dataapi.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * api参数
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("icredit_api_param")
public class IcreditApiParamEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String fieldName;

    private String fieldType;

    private Integer required;

    private String desc;

    private Integer isRequest;

    private Integer isResponse;

    private String apiBaseId;

    private String defaultValue;

    private Integer apiVersion;

}
