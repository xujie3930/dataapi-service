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
import org.apache.htrace.fasterxml.jackson.annotation.JsonFormat;

/**
 * <p>
 * API表基础信息表
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("icredit_api_base")
public class IcreditApiBaseEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String DEL_FLAG = "del_flag";
    public static final String NAME = "name";
    public static final String PATH = "path";
    public static final String PUBLISH_STATUS = "publish_status";
    public static final String TYPE = "type";
    public static final String PUBLISH_TIME = "publish_time";
    public static final String API_GROUP_ID = "api_group_id";

    /**
     * 主键id
     */
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date publishTime;

    //0-数据服务，1-资源管理
    private Integer interfaceSource;
}
