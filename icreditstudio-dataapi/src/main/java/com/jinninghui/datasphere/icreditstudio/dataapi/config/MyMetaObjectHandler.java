package com.jinninghui.datasphere.icreditstudio.dataapi.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.jinninghui.datasphere.icreditstudio.dataapi.common.SysUser;
import com.jinninghui.datasphere.icreditstudio.framework.result.base.BaseEntity;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @author Peng
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    ThreadLocal<SysUser> local = new ThreadLocal<>();

    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName(BaseEntity.CREATE_TIME, LocalDateTime.now(), metaObject);
        this.setFieldValByName(BaseEntity.CREATE_BY, local.get().getUserId(), metaObject);
        this.setFieldValByName(BaseEntity.DEL_FLAG, false, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName(BaseEntity.UPDATE_TIME, LocalDateTime.now(), metaObject);
        this.setFieldValByName(BaseEntity.UPDATE_BY, local.get().getUserId(), metaObject);
    }
}
