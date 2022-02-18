package com.jinninghui.datasphere.icreditstudio.framework.result;

import java.io.Serializable;
import java.util.Map;

/**
 * Project：business-build
 * Package：com.jinninghui.datasphere.framework.common
 * ClassName: IBaseObject
 * Description:  IBaseObject类
 * Date: 2021/5/26 1:42 下午
 *
 * @author liyanhui
 */
public interface IBaseObject extends Serializable {

    @Override
    String toString();

    Map<String, Object> toMap();

    void copyPropertiesTo(IBaseObject targetObject);

    void copyPropertiesFromObject(IBaseObject sourceObject);

    void copyPropertiesFromMap(Map<String, Object> sourceMap);

    <T extends IBaseObject> T createAndCopyProperties(Class<T> t);
}
