package com.ysl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@SpringBootApplication
@ServletComponentScan
@EnableTransactionManagement
@EnableCaching//pom 导入 缓存技术的以来坐标 spring-data-redis
public class ReigiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReigiApplication.class,args);
        log.info("项目启动成功");
    }
}
