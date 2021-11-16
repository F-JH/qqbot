package com.qqbot.www.qqBot.server;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class webSocketMsg {
    private List<List<String>> msgList = new CopyOnWriteArrayList<>();
    // 最多保存多少条
    private int limit = 60;
//    private List<String> newOne;
//    private boolean hasRead;

    public void put(List<String> msg){
        if(msgList.size()==limit){
            msgList.subList(0, 1).clear();
            msgList.add(msg);
        }else{
            msgList.add(msg);
        }
//        newOne = msg;
//        hasRead = false;
    }

    public List<List<String>> getALl(){
//        hasRead = true;
        return msgList;
    }

    public List<String> getLastOne(){
//        if(hasRead)
//            return null;
//        hasRead = true;
        return msgList.get(msgList.size() - 1);
    }
}
