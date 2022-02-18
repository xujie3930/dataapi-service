package com.jinninghui.datasphere.icreditstudio.framework.result;

import com.jinninghui.datasphere.icreditstudio.framework.result.base.BaseEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;

/**
 * Project：business-build
 * Package：com.jinninghui.datasphere.framework.common
 * ClassName: IBaseComponent
 * Description:  IBaseComponent类
 * Date: 2021/5/26 1:43 下午
 *
 * @author liyanhui
 */
public interface IBaseComponent extends ApplicationContextAware, ServletContextAware {

    void publishEvent(BaseEvent event);

    ApplicationContext getApplicationContext();

    ServletContext getServletContext();
}
