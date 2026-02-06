package com.trustfulfillment;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.trustfulfillment.mapper")
@EnableScheduling
public class TfApplication {

    public static void main(String[] args) {
        SpringApplication.run(TfApplication.class, args);
        System.out.println("=================================");
        System.out.println("  臻托 TrustFulfillment 启动成功!");
        System.out.println("  访问地址: http://localhost:8080");
        System.out.println("=================================");
    }
}
