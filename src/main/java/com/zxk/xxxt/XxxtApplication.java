package com.zxk.xxxt;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@MapperScan("com.zxk.xxxt.mapper")
@SpringBootApplication
public class XxxtApplication {

    public static void main(String[] args) {
        SpringApplication.run(XxxtApplication.class, args);
    }

}
