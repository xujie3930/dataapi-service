package com.jinninghui.datasphere.icreditstudio.framework.result.base;

import lombok.Data;

/**
 * Project：business-build
 * Package：com.jinninghui.datasphere.framework.common
 * ClassName: BaseRequest
 * Description:  BaseRequest类
 * Date: 2021/5/26 2:23 下午
 *
 * @author liyanhui
 */
@Data
public class BaseRequest extends BaseObject {

    //********************转换方法 start***********************//

    public <T extends BaseParam> T  toParam(Class<T> targetClazz){
        return super.createAndCopyProperties(targetClazz);
    }

    //********************转换方法 end***********************//

}
