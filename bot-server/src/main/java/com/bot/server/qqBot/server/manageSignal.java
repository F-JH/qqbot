package com.bot.server.qqBot.server;

import java.util.HashMap;
import java.util.Map;

public class manageSignal {
//    private boolean saveMessage;
    private Map<Integer, Boolean> saveMessageSignal = new HashMap<>();

    public void setAll(boolean saveMessage){
        for(Integer key:saveMessageSignal.keySet()){
            saveMessageSignal.put(key, saveMessage);
        }
    }

    public void setSaveMessage(Integer groupId, boolean saveMessage) {
        this.saveMessageSignal.put(groupId, saveMessage);
    }

    public boolean isSaveMessage(Integer groupId) {
        return saveMessageSignal.get(groupId);
    }
}
