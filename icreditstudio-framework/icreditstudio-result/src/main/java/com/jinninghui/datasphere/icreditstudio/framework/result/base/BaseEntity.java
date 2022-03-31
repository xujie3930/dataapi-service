package com.jinninghui.datasphere.icreditstudio.framework.result.base;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import org.apache.htrace.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * Project：business-build
 * Package：com.jinninghui.datasphere.framework.common
 * ClassName: BaseEntity
 * Description:  BaseEntity类
 * Date: 2021/5/26 2:23 下午
 *
 * @author liyanhui
 */
@Data
public class BaseEntity extends BaseObject {

    //********************常量字段 start************************//

    public static final String ID = "ID";
    public static final String DEL_FLAG = "del_flag";
    public static final String CREATE_TIME = "create_time";
    public static final String CREATE_BY = "create_by";
    public static final String UPDATE_TIME = "update_time";
    public static final String UPDATE_BY = "update_by";

    //********************常量字段 endt************************//

    //********************转换方法 start***********************//

    public <T extends BaseResult> T toResult(Class<T> targetClazz) {
        return super.createAndCopyProperties(targetClazz);
    }

    //********************转换方法 end***********************//

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "CST")
    private Date createTime;

    /**
     * 创建人
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "CST")
    private Date updateTime;

    /**
     * 更新人
     */
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 删除标识【0：未删除，1：已删除】
     */
    @TableField(value = "del_flag", fill = FieldFill.INSERT)
    @TableLogic
    private Integer delFlag = 0;
}
