package com.bot.admin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EnvGet {

    @Value("${env}")
    private String env;

    // 配置不同环境的配置文件地址
    private static final String testEnv = "bot-admin/src/main/resources/";
    private static final String prodEnv = "qqbot/bot-admin/src/main/resources/";

    public static String getByEnv(String envName) {
        if (envName.equals("test"))
            return testEnv;
        else if (envName.equals("prod"))
            return prodEnv;
        else
            return null;
    }

    public String getConfigPath(){
        if(env.equals("test"))
            return testEnv;
        else if(env.equals("prod"))
            return prodEnv;
        else
            return null;
    }
}
