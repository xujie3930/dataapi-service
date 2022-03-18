package com.jinninghui.datasphere.icreditstudio.dataapi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * @author Peng
 */
@MapperScan(basePackages = {"com.jinninghui.**.mapper"})
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(value = "com.jinninghui", excludeFilters = {
        @ComponentScan.Filter(type = FilterType.REGEX, pattern = {
                "com.jinninghui.datasphere.icreditstudio.framework.result.FilterConfig","com.jinninghui.datasphere.icreditstudio.framework.result.XssFilter"
        })
})
public class DataApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(DataApiApplication.class, args);
    }
}
