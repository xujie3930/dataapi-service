package com.jinninghui.datasphere.icreditstudio.dataapi.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author Peng
 */
@SpringBootApplication
@ComponentScan("com.jinninghui")
@EnableAsync
public class DataGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(DataGatewayApplication.class, args);
    }
}
