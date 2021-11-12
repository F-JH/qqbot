package com.qqbot.www.qqBot.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qqbot.www.qqBot.envGet;
import com.qqbot.www.qqBot.mybatis.service.groupMessageService;
import io.restassured.response.Response;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.qqbot.www.qqBot.mybatis.module.groupMessage;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.StampedLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.*;

@Component
public class msgManage {

    private final StampedLock stampedLock = new StampedLock();
    private final manageSignal mainSignal = new manageSignal();
    private final Map<Integer, Thread> groupThreads = new HashMap<>();
//    private final Map<Integer, manageSignal>
    private final ExecutorService pool = Executors.newFixedThreadPool(5);

    @Autowired
    private groupMessageService groupMessageService;

    @Value("${env}")
    private String env;

    private final messageData messageData = new messageData();

    int limit;

    public msgManage(){
        limit = 60;
    }

    public void put(Integer groupId, JSONObject msgJson, JSONObject configJson) throws InterruptedException{
        ConcurrentLinkedQueue<List<String>> groupQueue;
        if(!messageData.checkQueue(groupId)){
            // 启动新的线程、队列等
            if(messageData.putQueue(groupId, new ConcurrentLinkedQueue<List<String>>())){
                // 添加 msgList
                groupQueue = messageData.getQueue(groupId);
                List<List<String>> groupList = new LinkedList<>();
                messageData.putList(groupId, groupList);
                // 启动线程
                Thread groupThread = startThread(groupId, groupQueue, groupList);
                groupThreads.put(groupId, groupThread);
            }else groupQueue = messageData.getQueue(groupId);
            int result = groupMessageService.createTable(groupId.toString());
        }else{
            groupQueue = messageData.getQueue(groupId);
        }
        // 这回绝对安全了

        String postType = msgJson.getString("post_type");
        List<String> handleResult;
        if(configJson.getJSONArray("post_type").contains(postType))
             handleResult = handleMessage(msgJson);
        else
            return;
        if(handleResult.get(0).equals("")){
            Matcher m = Pattern.compile(" *").matcher(handleResult.get(3));
            if(m.find()) {
                if (handleResult.get(3).equals(m.group()) && handleResult.get(4).equals(""))
                    return;
            }
            if(handleResult.get(3).length() > 50){
                String text = handleResult.get(3);
                int range = (int) Math.ceil((double) text.length() / 50);
                for(int i=0; i<range; i++){
                    List<String> tmp = new ArrayList<String>();
                    tmp.add(handleResult.get(0));
                    tmp.add(handleResult.get(1));
                    tmp.add(handleResult.get(2));
                    tmp.add(new String(handleResult.get(3).substring(i*50, Math.min(i*50+50, text.length()))));
                    tmp.add(handleResult.get(4));
                    tmp.add(handleResult.get(5));
                    tmp.add(handleResult.get(6));
//                groupQueue.offer(JSONObject.parseArray(JSONObject.toJSONString(tmp)));
                    groupQueue.add(tmp);
                }
                return;
            }
        }

//        return groupQueue.offer(JSONArray.parseArray(JSONArray.toJSONString(handleResult)));
        try{
            groupQueue.add(handleResult);
        }catch(NullPointerException e){
            System.out.println("Debug in msgManage");
        }

    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getLimit() {
        return limit;
    }

    public List<String> handleMessage(JSONObject msgJson){
        String recallOperatorc;
        String messageId, userId;
        String rawMessage = "";
        String imageUrl = "";
        String reply = "";
        String now = String.valueOf(new Date().getTime());

        Pattern replyPattern = Pattern.compile("id=(.*?)\\]");
        Pattern imagePattern = Pattern.compile("url=(.*?)\\]");

        messageId = msgJson.getLong("message_id").toString();
        recallOperatorc = msgJson.getString("operator_id");

        // 被撤回消息
        if(recallOperatorc != null){
            userId = msgJson.getLong("user_id").toString();
            List<String> result = new ArrayList<String>();
            result.add(recallOperatorc);
            result.add(messageId);
            result.add(userId);
            result.add(rawMessage);
            result.add(imageUrl);
            result.add(now);
            result.add(reply);
            return result;
        }

        recallOperatorc = "";
        rawMessage = msgJson.getString("message");
        rawMessage = rawMessage.replace("\n", "");
        rawMessage = rawMessage.replace("\r", "");
        Matcher m = Pattern.compile("( *\\[CQ:.*?\\]) *").matcher(rawMessage);
        List<String> cqMatch = new ArrayList<String>();
        while(m.find())
            cqMatch.add(m.group(1));
        for(String s:cqMatch){
            // reply 提及
            if(s.contains("reply")){
                Matcher replyCher = replyPattern.matcher(s);
                if(replyCher.find())
                    reply = replyCher.group(1);
            }
            // image 包含一个或多个图片
            if(s.contains("image")){
                Matcher imageCher = imagePattern.matcher(s);
                if(imageCher.find())
                    if(imageUrl.equals(""))
                        imageUrl = imageCher.group(1);
                    else
                        imageUrl = imageUrl + "\n" + imageCher.group(1);
            }
            rawMessage = rawMessage.replace(s, "");
        }
        rawMessage = rawMessage.replace(",", "，");

        userId = msgJson.getJSONObject("sender").getLong("user_id").toString();
        List<String> result = new ArrayList<String>();
        result.add(recallOperatorc);
        result.add(messageId);
        result.add(userId);
        result.add(rawMessage);
        result.add(imageUrl);
        result.add(now);
        result.add(reply);
        return result;
    }

    public Thread startThread(Integer groupId,
                            ConcurrentLinkedQueue<List<String>> groupQueue,
                            List<List<String>> groupList){
        mainSignal.setSaveMessage(groupId, false);
        Thread task = new groupMain(limit, groupId, groupQueue, groupList, groupMessageService, mainSignal, env);
        task.start();
        return task;
    }

    public Map<String, List<List<String>>> getMsgList() {
        return messageData.getMsgList();
    }

    public void saveToMysqlSignal(){
        mainSignal.setAll(true);
    }

    public Map<String, String> getAllThreadStatus(){
        Map<String, String> result = new HashMap<>();
        for(Integer groupId:groupThreads.keySet())
            result.put(groupId.toString(), String.valueOf(groupThreads.get(groupId).isAlive()));
        return result;
    }

    public Map<String, String> getGroupThreadStatus(String groupId){
        Map<String, String> result = new HashMap<>();
        try{
            Integer gid = Integer.valueOf(groupId);
            Thread t = groupThreads.get(gid);
            if (t == null)
                result.put(groupId, "没有启动这个群的主线程");
            else
                result.put(groupId, String.valueOf(t.isAlive()));
        }catch (NumberFormatException e){
            result.put(groupId, "能不能好好输入群号");
        }
        return result;
    }
}









// 主运行任务

class groupMain extends Thread{

    private final String env;
    private final Integer limit;
    private final Integer groupId;
    private final List<List<String>> groupList;
    private final ConcurrentLinkedQueue<List<String>> groupQueue;
    private final groupMessageService gms;
    private final manageSignal mySignal;

    Logger logger = LoggerFactory.getLogger(getClass());

    public groupMain(Integer limit,
                     Integer groupId,
                     ConcurrentLinkedQueue<List<String>> groupQueue,
                     List<List<String>> groupList,
                     groupMessageService gms,
                     manageSignal mySignal,
                     String env){
        this.limit = limit;
        this.groupId = groupId;
        this.groupList = groupList;
        this.groupQueue = groupQueue;
        this.gms = gms;
        this.mySignal = mySignal;
        this.env = env;
    }

    @Override
    public void run(){
        long recordTime = new Date().getTime();
        boolean Repeater = true;
        int num;
        boolean isExist;
        String recallOperator, messageId, userId, rawMessage, imageUrl, createDate, reply;
        for(;;){
            // 处理 save message signal
            if(mySignal.isSaveMessage(groupId)){
                if(groupList.size() > 0)
                    saveMsgToMysql(groupList.size());
                mySignal.setSaveMessage(groupId, false);
            }

            num = groupList.size();
            if(num >= limit){
                saveMsgToMysql(30);
            }else if(new Date().getTime() - recordTime > 600000){
                recordTime = new Date().getTime();
                saveMsgToMysql(30);
            }
            List<String> sample;
            sample = groupQueue.poll();
            if(sample == null)
                continue;
            recallOperator = sample.get(0);
            messageId = sample.get(1);
            userId = sample.get(2);
            rawMessage = sample.get(3);
            imageUrl = sample.get(4);
            createDate = sample.get(5);
            reply = sample.get(6);

            // 处理撤回
            if(!recallOperator.equals("")){
                // 先在msgList内查找
                // num = groupList.size();
                boolean notFindInList = true;
                for(int i=num-1; i>=0; i--){
                    if(groupList.get(i).get(1).equals(messageId)){
                        groupList.get(i).set(0, recallOperator);
                        notFindInList = false;
                        break;
                    }
                }
                if(notFindInList){
                    try{
                        gms.setRecall(groupId.toString(), recallOperator, messageId);
                    }catch (SQLException e){
                        logger.info("处理撤回信息出现错误：", e);
                    }
                }
                continue;
            }

            List<String> tmpMsg = new ArrayList<>();
            tmpMsg.add(recallOperator);
            tmpMsg.add(messageId);
            tmpMsg.add(userId);
            tmpMsg.add(rawMessage);
            tmpMsg.add(imageUrl);
            tmpMsg.add(createDate);

            if(!reply.equals("") && num > 0){
                isExist = false;
                for(int n=num-1; n>Math.max(num-30, -1); n--){
                    if(groupList.get(n).get(1).equals(reply)){
                        isExist = true;
                        groupList.add(n+1, tmpMsg);
                        break;
                    }
                }
                if(!isExist)
                    groupList.add(tmpMsg);
            }else{
                String lastMsg = null;
                if(groupList.size()>0)
                    lastMsg = groupList.get(groupList.size() - 1).get(3);
                groupList.add(tmpMsg);
                // 复读机
                if((!rawMessage.equals("")) && rawMessage.equals(lastMsg) && Repeater){
                    Repeater = false;
                    File file = new File(envGet.getByEnv(env) + "config/config.json");
                    JSONObject configJson;
                    try{
                        String data = FileUtils.readFileToString(file);
                        configJson = JSON.parseObject(data);
                    }catch(IOException e){
                        logger.error("读取配置文件出错，取消复读: ", e);
                        continue;
                    }
                    if(configJson.getJSONArray("Repeater").contains(groupId.toString())){
                        String url = String.format("http://127.0.0.1:5700/send_group_msg?group_id=%d&message=%s", groupId, rawMessage);
                        Response res = post(url);
                        System.out.println("\033[1;31m" +
                                String.join("", Collections.nCopies(40, "-")) +
                                String.format("复读鸡启动：%d|%d|%s", res.statusCode(), groupId, rawMessage) +
                                String.join("", Collections.nCopies(40, "-")) +
                                "\033[0m");
                    }
                }else if(!rawMessage.equals(lastMsg))
                    Repeater = true;
            }
        }
    }

    private void saveMsgToMysql(int num){
        if(groupList.size() == 0) {
            System.out.println(String.format("%d Save 0 message to mysql success", groupId));
            return;
        }
        if(groupList.size() < 30)
            num = groupList.size();
        List<List<String>> tmp = groupList.subList(0, num);
        List<groupMessage> msgs = new ArrayList<>();
        for(List<String> iter:tmp){
            groupMessage gm = new groupMessage(iter.get(0), iter.get(1), iter.get(2), iter.get(3), iter.get(4), new Date(Long.parseLong(iter.get(5))));
            msgs.add(gm);
        }
        try{
            gms.add(groupId.toString(), msgs);
            System.out.println(String.format("%d Save %d message to mysql success", groupId, num));
            tmp.clear();
        }catch(SQLException e){
            logger.info("[" + groupId + "]" + "线程自动插入出错：", e);
        }
    }
}