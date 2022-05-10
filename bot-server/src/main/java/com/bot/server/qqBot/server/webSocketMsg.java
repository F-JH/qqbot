package com.bot.server.qqBot.server;

import com.alibaba.fastjson.JSON;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class webSocketMsg {
    private List<List<String>> msgList = new CopyOnWriteArrayList<>();
    // 最多保存多少条
    private int limit = 200;
    // 巡检session列表
    private final List<WebSocketSession> inspection = new CopyOnWriteArrayList<>();

    public void put(List<String> msg){
        if(msgList.size()==limit){
            msgList.subList(0, 1).clear();
            msgList.add(msg);
        }else{
            msgList.add(msg);
        }
        for(WebSocketSession session:inspection){
            if(!session.isOpen()){
                inspection.remove(session);
                continue;
            }
            try{
                session.sendMessage(new TextMessage(JSON.toJSONString(msg)));
            }catch (IOException e){
                System.out.println(session.getId() + "：似乎出了点问题。。。");
            }
        }
    }

    public List<List<String>> getALl(){
        return msgList;
    }

    public List<String> getLastOne(){
        return msgList.get(msgList.size() - 1);
    }

    public void putSession(WebSocketSession session){
        inspection.add(session);
    }

    public boolean removeSession(WebSocketSession session){
        return inspection.remove(session);
    }
}
