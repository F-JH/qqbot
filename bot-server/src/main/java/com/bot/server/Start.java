package com.bot.server;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
//@DubboComponentScan(basePackages = "com.bot.server.qqBot.Beans")
//@MapperScan("com.bot.server.qqBot.mybatis.dao")
public class Start {
    public static void main(String[] args) {
        SpringApplication.run(Start.class, args);
    }
}
