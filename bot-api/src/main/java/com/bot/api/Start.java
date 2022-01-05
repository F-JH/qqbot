package com.bot.api;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
//@MapperScan("com.bot.api.qqBot.mybatis.dao")
public class Start {
    public static void main(String[] args) {
        SpringApplication.run(Start.class, args);
    }
}
