package com.jinninghui.datasphere.icreditstudio.framework.result.base;

import lombok.Data;

/**
 * Project：business-build
 * Package：com.jinninghui.datasphere.framework.common
 * ClassName: BaseParam
 * Description:  BaseParam类
 * Date: 2021/5/26 2:23 下午
 *
 * @author liyanhui
 */
@Data
public class BaseParam extends BaseObject {

    //********************转换方法 start***********************//

    public <T extends BaseEntity> T  toEntity(Class<T> targetClazz){
        return super.createAndCopyProperties(targetClazz);
    }

    //********************转换方法 end***********************//

}
