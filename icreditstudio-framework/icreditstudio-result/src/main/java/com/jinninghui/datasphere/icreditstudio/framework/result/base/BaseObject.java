package com.jinninghui.datasphere.icreditstudio.framework.result.base;

import com.jinninghui.datasphere.icreditstudio.framework.result.IBaseObject;
import com.jinninghui.datasphere.icreditstudio.framework.utils.ReflectUtils;

import java.util.Map;

/**
 * Project：business-build
 * Package：com.jinninghui.datasphere.framework.common
 * ClassName: BaseObject
 * Description:  BaseObject类
 * Date: 2021/5/26 2:21 下午
 *
 * @author liyanhui
 */
public class BaseObject implements IBaseObject {

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getName())
                .append(":")
                .append(ReflectUtils.getPropertyValues(this));
        return builder.toString();
    }

    @Override
    public Map<String, Object> toMap() {
        return (Map<String, Object>) ReflectUtils.bean2Map(this);
    }

    @Override
    public void copyPropertiesTo(IBaseObject targetObject) {
        ReflectUtils.copyPropertiesFromBean(this, targetObject);
    }

    @Override
    public void copyPropertiesFromObject(IBaseObject sourceObject) {
        ReflectUtils.copyPropertiesFromBean(sourceObject, this);
    }

    @Override
    public void copyPropertiesFromMap(Map<String, Object> sourceMap) {
        ReflectUtils.copyPropertiesFromMap(this, sourceMap);
    }

    @Override
    public <T extends IBaseObject> T createAndCopyProperties(Class<T> t) {
        return ReflectUtils.createAndCopyProperties(this, t);
    }
}
