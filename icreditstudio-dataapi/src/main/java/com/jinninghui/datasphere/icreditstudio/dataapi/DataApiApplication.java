package com.jinninghui.datasphere.icreditstudio.dataapi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author Peng
 */
@MapperScan(basePackages = {"com.jinninghui.**.mapper"})
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class DataApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(DataApiApplication.class, args);
    }
}
