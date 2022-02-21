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
 * 业务流程
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("icredit_work_flow")
public class IcreditWorkFlowEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String DEL_FLAG = "del_flag";
    public static final String SORT = "sort";
    /**
     * 主键id
     */
    @TableId(type = IdType.ID_WORKER_STR)
    private String id;

    private String name;

    @TableField("`desc`")
    private String desc;

    /**
     * 用于目录树排序
     */
    private Integer sort = 0;

//    private String remark;
//
//    private Date createTime;
//
//    private String createBy;
//
//    private Date updateTime;
//
//    private String updateBy;
//
//    private Integer delFlag;


}
