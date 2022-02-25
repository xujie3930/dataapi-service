package com.jinninghui.datasphere.icreditstudio.dataapi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.jinninghui.datasphere.icreditstudio.framework.result.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 授权配置
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("icredit_auth_config")
public class IcreditAuthConfigEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @TableId(type = IdType.ID_WORKER_STR)
    private String id;

    private Long periodBegin;

    private Long periodEnd;

    private Integer allowCall;
}
