package com.jinninghui.datasphere.icreditstudio.dataapi.gateway.configuration;

import com.jinninghui.datasphere.icreditstudio.dataapi.gateway.interceptor.DataApiGatewayInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Project：icreditstudio-dataapi-service
 * Package：com.jinninghui.datasphere.icreditstudio.dataapi.gateway.configuration
 * ClassName: InterceptorConfiguration
 * Description:  InterceptorConfiguration类
 * Date: 2022/3/17 10:37 上午
 *
 * @author liyanhui
 */
@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer {

    @Autowired
    private DataApiGatewayInterceptor dataApiGatewayInterceptor;

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(dataApiGatewayInterceptor).addPathPatterns("/openapi/**");

    }
}
