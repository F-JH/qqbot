package com.bot.api.qqBot.Beans;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bot.api.qqBot.server.msgManage;
import com.bot.api.qqBot.mapper.noticeMethod;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import com.bot.api.qqBot.scripts.botApi;

import java.util.*;

import static io.restassured.RestAssured.*;

@Component
public class noticeMethodBean {
    @Bean
    public Map<String, noticeMethod> noticeMethodMap(){
        Map<String, noticeMethod> method = new HashMap<>();
        method.put("group_recall", new saveRecall());
        method.put("group_increase", new newMember());
        method.put("group_decrease", new memberLeave());
        return method;
    }
}

class saveRecall implements noticeMethod{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public String run(JSONObject msg, JSONObject configJson, Integer groupId, msgManage manage){
        try{
            manage.put(groupId, msg, configJson);
            Integer messageId = msg.getInteger("message_id");
            logger.info(String.format("群[%d]撤回消息: %d", groupId, messageId));
            return "OK";
        }catch (InterruptedException e){
            logger.info("failed to recall", e);
            return "failed to recall";
        }
    }
}

class newMember implements noticeMethod{
    @Override
    public String run(JSONObject msg, JSONObject configJson, Integer groupId, msgManage manage){
        long now = new Date().getTime();
        String userId = msg.getString("user_id");
        JSONArray welcomeUsers = configJson.getJSONObject("welcome_group").getJSONArray(groupId.toString());
        boolean isWelcome = false;
        boolean send = false;
        if(welcomeUsers == null)
            return "This group is not focus on.";
        if(welcomeUsers.size() == 0){
            // 没有设置长老则默认发送
            isWelcome = true;
            send = true;
        }
        while(!isWelcome){
            int num;
            List<List<String>> groupList = manage.getMsgList().get(groupId.toString());
            if(groupList == null)
                num = 0;
            else
                num = groupList.size();
            for(int i=num-1; i>=0; i--){
                try{
                    List<String> msgTmp = groupList.get(i);
                    if(welcomeUsers.contains(msgTmp.get(2)) && msgTmp.get(3).contains("欢迎")){
                        isWelcome = true;
                        send = true;
                        break;
                    }
                }catch (IndexOutOfBoundsException e){
                    break;
                }
            }
            if(new Date().getTime() - now > 15000)
                break;
        }
        if(send){
            JSONArray imgs = configJson.getJSONObject("welcome_group").getJSONObject("image").getJSONArray(groupId.toString());
            String img = imgs.getString((int) (Math.random() * imgs.size()));
            if(img == null)
                return "should set images";
            botApi bot = new botApi(configJson.getString("BOTROOT"));
            Response res = bot.sendGroupImage(groupId.toString(), img);
//            String sendMsg = String.format("[CQ:image,file=http://127.0.0.1:8080/%s]", imgs.getString());
//            String url = configJson.getString("BOTROOT") + String.format("/send_group_msg?group_id=%d&message=%s", groupId, sendMsg);
//            Response res = get(url);
            System.out.println(
                    "\033[1;31m"+
                    String.join("", Collections.nCopies(40, "-")) +
                    String.format("%s发现新入侵者: %s，已发送警告!", configJson.getJSONObject("focusGroup").getString(groupId.toString()), userId) +
                    String.join("", Collections.nCopies(40, "-")) +
                    "\033[0m"
            );
        }else{
            System.out.println(
                    "\033[1;31m"+
                    String.join("", Collections.nCopies(40, "-")) +
                    String.format("%s发现新入侵者: %s，经议会审议，不发送警告。长老会议员：%s", configJson.getJSONObject("focusGroup").getString(groupId.toString()), userId, welcomeUsers) +
                    String.join("", Collections.nCopies(40, "-")) +
                    "\033[0m"
            );
        }

        return "group increase";
    }
}

class memberLeave implements noticeMethod{
    @Override
    public String run(JSONObject msg, JSONObject configJson, Integer groupId, msgManage manage){
        System.out.println(
                "\033[1;31m" +
                 configJson.getJSONObject("focusGroup").getString(groupId.toString()) +
                 "有人跑路："+ msg.getString("user_id") + "\033[0m");
        botApi bot = new botApi(configJson.getString("BOTROOT"));
        String message;
        if(msg.getString("sub_type").equals("kick"))
            message = String.format("【%s】被送走了~", msg.getString("user_id"));
        else
            message = String.format("【%s】离开了我们~", msg.getString("user_id"));
        bot.sendGroupMsg(groupId.toString(), message);
        return "group decrease";
    }
}