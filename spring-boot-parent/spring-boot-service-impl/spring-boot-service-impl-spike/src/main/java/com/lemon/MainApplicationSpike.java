package com.lemon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author ：lemon
 * @description：秒杀启动程序
 * @date ：Created in 2020/4/30 17:03
 */
@SpringBootApplication
@MapperScan(basePackages = "com.lemon.spike.mapper")
public class MainApplicationSpike {
    public static void main(String[] args) {
        SpringApplication.run(MainApplicationSpike.class, args);
    }
}
