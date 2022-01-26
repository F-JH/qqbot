package com.bot.api.qqBot.scripts;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bot.api.qqBot.envGet;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class GetConfig {
    public static JSONObject config(String env){
        JSONObject configJson = null;
        File file = new File(envGet.getByEnv(env) + "config/config.json");
        try{
            String data = FileUtils.readFileToString(file);
            configJson = JSON.parseObject(data);
        }catch(IOException e){
            System.out.println("读取配置文件出错");
        }
        return configJson;
    }
}
