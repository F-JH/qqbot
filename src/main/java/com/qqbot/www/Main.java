package com.qqbot.www;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.qqbot.www.qqBot.mybatis.dao")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
