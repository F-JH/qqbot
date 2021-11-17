package com.qqbot.www.qqBot.Beans;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.*;

import com.qqbot.www.qqBot.server.msgManage;
import com.qqbot.www.qqBot.server.webSocketMsg;

import java.util.List;

public class webSocketBean implements WebSocketHandler {

    @Autowired
    private msgManage manage;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception{

    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception{
        String groupId = message.getPayload().toString();
        String lastMessageId = "初始值";
        if(!groupId.equals("")){
            webSocketMsg wsm = manage.getWSM(groupId);
            if(wsm == null){
                session.sendMessage(new TextMessage("没有数据"));
                return;
            }
            List<List<String>> msg = wsm.getALl();
            for(int i=0;i<msg.size();i++){
                String m = JSON.toJSONString(msg.get(i));
                WebSocketMessage<String> s = new TextMessage(m);
                session.sendMessage(s);
                lastMessageId = msg.get(i).get(1);
            }
            for(;;){
                List<String> lastOne = wsm.getLastOne();
                if(!lastMessageId.equals(lastOne.get(1))){
                    lastMessageId = lastOne.get(1);
                    session.sendMessage(new TextMessage(JSON.toJSONString(lastOne)));
                }
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception{

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception{

    }

    @Override
    public boolean supportsPartialMessages(){
        return true;
    }
}

