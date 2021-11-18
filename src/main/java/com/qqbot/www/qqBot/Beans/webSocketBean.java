package com.qqbot.www.qqBot.Beans;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.*;

import com.qqbot.www.qqBot.server.msgManage;
import com.qqbot.www.qqBot.server.webSocketMsg;

import java.net.http.WebSocket;
import java.util.List;

public class webSocketBean implements WebSocketHandler {

    @Autowired
    private msgManage manage;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception{
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception{
        // {
        //     "code": 200 已接收过全量消息，请求加入自动巡检、 100 心跳包、300 未收到过任何消息
        //     "groupId": group id,
        //     "timestamp": 心跳包时间戳
        // }
        String data = message.getPayload().toString();
        String lastMessageId = "初始值";
        if(!data.equals("")){
            JSONObject jsonData = JSON.parseObject(data);
            String groupId = jsonData.getString("groupId");
            Integer code = jsonData.getInteger("code");
            if(code.equals(100))
                return;
            webSocketMsg wsm = manage.getWSM(groupId);
            if(wsm == null){
                session.sendMessage(new TextMessage("没有这个群的数据"));
                return;
            }
            if(code.equals(300)){
                List<List<String>> msg = wsm.getALl();
                for(int i=0;i<msg.size();i++){
                    String m = JSON.toJSONString(msg.get(i));
                    WebSocketMessage<String> s = new TextMessage(m);
                    session.sendMessage(s);
//                    lastMessageId = msg.get(i).get(1);
                }
                WebSocketMessage<String> s = new TextMessage("end");
                session.sendMessage(s);
            }
            if(code.equals(200)){
                wsm.putSession(session);
            }
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception{
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception{
        System.out.println("连接断开...............");
        manage.removeSession(session);
    }

    @Override
    public boolean supportsPartialMessages(){
        return true;
    }
}

