package com.jinninghui.datasphere.icreditstudio.dataapi.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author Peng
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    private static final String FIELD_CREATE_TIME = "createTime";
    public static final String FIELD_CREATE_BY = "createBy";
    public static final String FIELD_DEL_FLAG = "delFlag";
    public static final String FIELD_UPDATE_TIME = "updateTime";
    public static final String FIELD_UPDATE_BY = "updateBy";

//    ThreadLocal<SysUser> local = new ThreadLocal<>();

    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByName(FIELD_CREATE_TIME, new Date(), metaObject);
        this.setFieldValByName(FIELD_CREATE_BY, null/*local.get().getUserId()*/, metaObject);
        this.setFieldValByName(FIELD_DEL_FLAG, false, metaObject);
        this.setFieldValByName(FIELD_UPDATE_TIME, new Date(), metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName(FIELD_UPDATE_TIME, new Date(), metaObject);
        this.setFieldValByName(FIELD_UPDATE_BY, null/*local.get().getUserId()*/, metaObject);
    }
}
