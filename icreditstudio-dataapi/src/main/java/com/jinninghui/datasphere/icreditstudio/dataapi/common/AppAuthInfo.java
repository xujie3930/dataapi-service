package com.jinninghui.datasphere.icreditstudio.dataapi.common;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jinninghui.datasphere.icreditstudio.framework.result.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

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
public class AppAuthInfo extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private String id;

    private String appFlag;

    private Integer certificationType;

    private Integer isEnable;

    private String name;

    private String secretContent;

    private String appGroupId;

    private String desc;

    private String token;

    private Integer period;

    private String allowIp;

    private Long periodBegin;

    private Long periodEnd;

    private Integer allowCall;
}
