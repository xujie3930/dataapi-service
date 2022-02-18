package com.jinninghui.datasphere.icreditstudio.framework.exception;

import com.jinninghui.datasphere.icreditstudio.framework.result.base.BaseEvent;

/**
 * Project：business-build
 * Package：com.jinninghui.datasphere.framework.exception.event
 * ClassName: ExceptionEvent
 * Description:  ExceptionEvent类
 * Date: 2021/5/31 3:33 下午
 *
 * @author liyanhui
 */
public abstract class BaseExceptionEvent extends BaseEvent {
    /**
     * 创建一个新的事件
     *
     * @param source 事件发生时的对象
     */
    public BaseExceptionEvent(Object source) {
        super(source);
    }
}
