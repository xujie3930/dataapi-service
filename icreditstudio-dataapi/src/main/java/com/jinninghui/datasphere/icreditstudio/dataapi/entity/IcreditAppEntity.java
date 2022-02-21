package com.jinninghui.datasphere.icreditstudio.dataapi.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 应用
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("icredit_app")
public class IcreditAppEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String appFlag;

    private Boolean certificationType;

    private Boolean isEnable;

    private String name;

    private String secretContent;

    private String appGroupId;

    private String desc;

    private String token;

    private Integer period;

    private String allowIp;

    private String remark;

    private LocalDateTime createTime;

    private String createBy;

    private LocalDateTime updateTime;

    private String updateBy;

    private Boolean delFlag;


}
