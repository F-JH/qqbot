package com.bot.server.qqBot.scripts;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bot.server.qqBot.envGet;
import com.bot.server.qqBot.server.msgManage;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@Component
@Configuration
public class CheckAlive{
    @Autowired
    private msgManage manage;

    @Value("${env}")
    private String env;

    @Scheduled(fixedDelay = 3000)
    public void run(){
        File file = new File(envGet.getByEnv(env) + "config/config.json");
        JSONObject configJson;
        try{
            String data = FileUtils.readFileToString(file);
            configJson = JSON.parseObject(data);
        }catch(IOException e){
            throw new RuntimeException(e);
        }

        botApi bot = new botApi(configJson.getString("BOTROOT"));
        Map<String, Boolean> status = manage.getAllThreadStatus();
        for(String groupId:status.keySet()){
            if(!status.get(groupId)){
                bot.sendGroupMsg(
                        "961530103",
                        String.format("检测到【%s】群被敌方入侵，已失去联系！", configJson.getJSONObject("focusGroup").getString(groupId))
                );
                manage.removeDeadThread(Integer.valueOf(groupId));
            }
        }
    }
}
