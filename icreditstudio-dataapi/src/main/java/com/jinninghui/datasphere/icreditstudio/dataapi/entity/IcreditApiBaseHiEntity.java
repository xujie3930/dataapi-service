package com.jinninghui.datasphere.icreditstudio.dataapi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Date;

import com.jinninghui.datasphere.icreditstudio.framework.result.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * API表基础信息历史版本表
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("icredit_api_base_hi")
public class IcreditApiBaseHiEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ID_WORKER_STR)
    private String id;

    private Integer type;

    private String name;

    private String path;

    private String requestType;

    private String responseType;

    @TableField("`desc`")
    private String desc;

    private String apiGroupId;

    private Integer apiVersion;

    private Integer publishStatus;

    private String publishUser;

    private Date publishTime;

    private String apiBaseId;

    //0-数据服务，1-资源管理
    private Integer interfaceSource;

}
