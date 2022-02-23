package com.jinninghui.datasphere.icreditstudio.dataapi.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 生成API表
 * </p>
 *
 * @author xujie
 * @since 2022-02-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("icredit_generate_api")
public class IcreditGenerateApiEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer model;

    private String datasourceId;

    private String tableName;

    private String sql;

    private String apiBaseId;

    private Integer apiVersion;

}
