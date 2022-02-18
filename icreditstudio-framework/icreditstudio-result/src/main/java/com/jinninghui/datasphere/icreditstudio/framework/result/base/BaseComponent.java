package com.jinninghui.datasphere.icreditstudio.framework.result.base;

import com.jinninghui.datasphere.icreditstudio.framework.result.IBaseComponent;
import com.jinninghui.datasphere.icreditstudio.framework.sequence.api.SequenceService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.servlet.ServletContext;

/**
 * Project：business-build
 * Package：com.jinninghui.datasphere.framework.common.component
 * ClassName: BaseComponent
 * Description:  BaseComponent类
 * Date: 2021/5/26 2:25 下午
 *
 * @author liyanhui
 */
public class BaseComponent implements IBaseComponent {

    private ApplicationContext applicationContext;
    private ServletContext servletContext;
    @Autowired(required = false)
    private SequenceService sequenceService;


    @Override
    public void publishEvent(BaseEvent event) {
        applicationContext.publishEvent(event);
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public ServletContext getServletContext() {
        return this.servletContext;
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public SequenceService getSequenceService(){
        if(sequenceService != null){
            return sequenceService;
        }
        throw new IllegalStateException("SequenceService未正常启用，请确认已经使用@EnableSequenceService注解开启");
    }
}
