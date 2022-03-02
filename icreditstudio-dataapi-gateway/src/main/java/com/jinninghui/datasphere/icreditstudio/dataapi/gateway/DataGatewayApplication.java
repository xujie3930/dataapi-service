package com.jinninghui.datasphere.icreditstudio.dataapi.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author Peng
 */
@SpringBootApplication
@ComponentScan("com.jinninghui")
public class DataGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(DataGatewayApplication.class, args);
    }
}
