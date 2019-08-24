package com.chen.communit;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan(basePackages = "com.chen.communit.mapper")
public class CommunitApplication {

    public static void main(String[] args) {

        SpringApplication.run(CommunitApplication.class, args);
    }

}
