package com.bot.admin.config.method;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bot.admin.config.EnvGet;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    public static JSONObject getNavigation(EnvGet envGet){
        JSONObject navigation;
        File file = new File(envGet.getConfigPath() + "config/index.json");
        try{
            String data = FileUtils.readFileToString(file);
            navigation = JSON.parseObject(data);
            return navigation;
        }catch(IOException e){
            logger.error("读取配置文件出错", e);
            return null;
        }
    }
}
