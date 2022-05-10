package com.bot.api;

import org.apache.dubbo.config.spring.context.annotation.DubboComponentScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@DubboComponentScan(basePackages = "com.bot.api.service")
public class BotApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(BotApiApplication.class);
    }
}
