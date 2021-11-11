package com.qqbot.www.qqBot.Beans;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import com.qqbot.www.qqBot.mapper.postMethod;
import com.qqbot.www.qqBot.mapper.noticeMethod;
import com.qqbot.www.qqBot.server.msgManage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class postMethodBean {

    @Autowired
    private msgManage manage;

    @Autowired
    private Map<String, noticeMethod> noticeMethodMap;

    @Bean
    public Map<String, postMethod> postMethodMap(){
        Map<String, postMethod> method = new HashMap<>();
        saveMessage sm = new saveMessage(manage);
        notice n = new notice(manage, noticeMethodMap);
        method.put("message", sm);
        method.put("message_sent", sm);
        method.put("notice", n);
        return method;
    }
}

class saveMessage implements postMethod{

    @Autowired
    private final msgManage manage;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public saveMessage(msgManage manage){
        this.manage = manage;
    }

    @Override
    public String run(JSONObject msg, JSONObject configJson, Integer groupId){
        if(configJson.getJSONArray("printGroup").contains(groupId)){
            String name = msg.getJSONObject("sender").getString("card");
            if(name.equals(""))
                name = msg.getJSONObject("sender").getString("nickname");
            String messageId = msg.getString("message_id");
            String message = msg.getString("message");
            String groupName = configJson.getJSONObject("focusGroup").getString(groupId.toString());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            String printMsg = "\033[1;32m" + simpleDateFormat.format(new Date()) + "|" + "[" + groupName + "]" + name + "(" + messageId + "): " + message;
            System.out.println(printMsg);
        }
//        logger.info(String.format("%s(%s): %s", name, messageId, message));
        try {
            manage.put(groupId, msg, configJson);
            return "OK";
        }catch(InterruptedException e){
            logger.info("Failed to put: ", e);
            return "Failed";
        }
    }
}

class notice implements postMethod{

    private final msgManage manage;
    private final Map<String, noticeMethod> noticeMethodMap;

    public notice(msgManage manage, Map<String, noticeMethod> noticeMethodMap){
        this.manage = manage;
        this.noticeMethodMap = noticeMethodMap;
    }

    @Override
    public String run(JSONObject msg, JSONObject configJson, Integer groupId){
        String noticeType = msg.getString("notice_type");
        if(noticeType == null)
            return "Can not found notice_type";
        noticeMethod method = noticeMethodMap.get(noticeType);
        if(method == null)
            return "we do not handle this notice type";
        return method.run(msg, configJson, groupId, manage);
    }
}