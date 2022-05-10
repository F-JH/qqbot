package com.bot.server.qqBot.server;

import java.util.*;
//import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.StampedLock;

public class messageData {
    private final StampedLock stampedLockQueue = new StampedLock();
    private final StampedLock stampedLockList = new StampedLock();

    private final Map<Integer, ConcurrentLinkedQueue<List<String>>> msgQueue = new HashMap<>();
    private final Map<String, List<List<String>>> msgList = new HashMap<>();

    public Map<String, List<List<String>>> getMsgList() {
        return msgList;
    }

    public boolean checkQueue(Integer groupId){
        long stamp = stampedLockQueue.tryOptimisticRead();
        ConcurrentLinkedQueue<List<String>> groupQueue = msgQueue.get(groupId);
        if(!stampedLockQueue.validate(stamp)){
            stamp = stampedLockQueue.readLock();
            try{
                groupQueue = msgQueue.get(groupId);
            }finally {
                stampedLockQueue.unlockRead(stamp);
            }
        }
        if(groupQueue == null)
            return false;
        return true;
    }

    public boolean putQueue(Integer groupId, ConcurrentLinkedQueue<List<String>> groupQueue){
        long stamp = stampedLockQueue.writeLock();
        try{
            if(msgQueue.get(groupId) == null){
                msgQueue.put(groupId, groupQueue);
                return true;
            }
            return false;
        }finally {
            stampedLockQueue.unlockWrite(stamp);
        }
    }

    public boolean checkList(Integer groupId){
        long stamp = stampedLockList.tryOptimisticRead();
        List<List<String>> groupList = msgList.get(groupId.toString());
        if(!stampedLockQueue.validate(stamp)){
            stamp = stampedLockList.readLock();
            try {
                groupList = msgList.get(groupId.toString());
            }finally {
                stampedLockList.unlockRead(stamp);
            }
        }
        if(groupList == null)
            return false;
        return true;
    }

    public void putList(Integer groupId, List<List<String>> groupList){
        long stamp = stampedLockList.writeLock();
        try {
            msgList.put(groupId.toString(), groupList);
        }finally {
            stampedLockList.unlockWrite(stamp);
        }
    }

    public ConcurrentLinkedQueue<List<String>> getQueue(Integer groupId){
        return msgQueue.get(groupId);
    }

    public List<List<String>> getList(Integer groupId){
        return msgList.get(groupId.toString());
    }
}