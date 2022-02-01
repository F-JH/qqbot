package com.bot.api.qqBot.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bot.api.qqBot.envGet;
import com.bot.api.qqBot.mybatis.service.groupMessageService;
import com.bot.api.qqBot.scripts.GetConfig;
import io.restassured.response.Response;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.bot.api.qqBot.mybatis.module.groupMessage;
import org.springframework.web.socket.WebSocketSession;
import com.bot.api.qqBot.scripts.botApi;

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
    private final Map<String, webSocketMsg> groupWebSocketMsg = new HashMap<>();
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
                webSocketMsg groupWSM = new webSocketMsg();
                groupWebSocketMsg.put(groupId.toString(), groupWSM);
                // 启动线程
                String groupName = configJson.getJSONObject("focusGroup").getString(groupId.toString());
                Thread groupThread = startThread(groupId, groupQueue, groupList, groupName, groupWSM);
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
             handleResult = handleMessage(msgJson, configJson, groupId);
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
                    tmp.add(handleResult.get(7));
                    tmp.add(handleResult.get(8));
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

    public List<String> handleMessage(JSONObject msgJson, JSONObject configJson, Integer groupId){
        String recallOperatorc;
        String messageId;
        String userId = "";
        String rawMessage = "";
        String imageUrl = "";
        String reply = "";
        String now = String.valueOf(new Date().getTime());
        String userName = "";
        String at = "";
        List<String> ats = new ArrayList<>();

        Pattern atPattern = Pattern.compile("qq=(.*?)\\]");
        Pattern replyPattern = Pattern.compile("id=(.*?)\\]");
        Pattern imagePattern = Pattern.compile("url=(.*?)\\,");
        Pattern imageType = Pattern.compile("type=(.*?)\\,");
        Pattern imageFile = Pattern.compile("file=(.*?)\\.image");

        messageId = msgJson.getLong("message_id").toString();
        recallOperatorc = msgJson.getString("operator_id");
//        String img;

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
            result.add(userName);
            result.add(at);
            return result;
        }

        recallOperatorc = "";
        userId = msgJson.getJSONObject("sender").getLong("user_id").toString();

        rawMessage = msgJson.getString("message");
        rawMessage = rawMessage.replace("\n", "");
        rawMessage = rawMessage.replace("\r", "");
        Matcher m = Pattern.compile("( *\\[CQ:.*?\\]) *").matcher(rawMessage);
        List<String> cqMatch = new ArrayList<String>();
        while(m.find())
            cqMatch.add(m.group(1));
        for(String s:cqMatch){
            if(s.contains("CQ:at")){
                // at某人
                Matcher atCher = atPattern.matcher(s);
                if(atCher.find())
                    ats.add(atCher.group(1));
            }
            // reply 提及
            if(s.contains("CQ:reply")){
                Matcher replyCher = replyPattern.matcher(s);
                if(replyCher.find())
                    reply = replyCher.group(1);
            }
            // image 包含一个或多个图片
            if(s.contains("CQ:image")){
                Matcher typeCher = imageType.matcher(s);
                if(typeCher.find()){
                    // 闪照
                    Matcher fileCher = imageFile.matcher(s);
                    fileCher.find();    // 一定能匹配上
                    String file = fileCher.group(1);
                    file = file.toUpperCase();
                    imageUrl = String.format(configJson.getString("imgUrlTemple"), userId, groupId, file);
                }else{
                    // 普通照片
                    Matcher imageCher = imagePattern.matcher(s);
                    imageCher.find();
                    if(imageUrl.equals(""))
                        imageUrl = imageCher.group(1);
                    else
                        imageUrl = imageUrl + "\n" + imageCher.group(1);
                }
            }
            rawMessage = rawMessage.replace(s, "");
        }
        at = String.join(",", ats);
        rawMessage = rawMessage.replace(",", "，");


        if(msgJson.getJSONObject("sender").getString("card").equals(""))
            userName = msgJson.getJSONObject("sender").getString("nickname");
        else
            userName = msgJson.getJSONObject("sender").getString("card");
        List<String> result = new ArrayList<String>();
        result.add(recallOperatorc);
        result.add(messageId);
        result.add(userId);
        result.add(rawMessage);
        result.add(imageUrl);
        result.add(now);
        result.add(reply);
        result.add(userName);
        result.add(at);
        return result;
    }

    public Thread startThread(Integer groupId,
                            ConcurrentLinkedQueue<List<String>> groupQueue,
                            List<List<String>> groupList,
                            String groupName, webSocketMsg wsm){
        mainSignal.setSaveMessage(groupId, false);
        Thread task = new groupMain(limit, groupId, groupQueue, groupList, groupMessageService, mainSignal, env, wsm);
        task.setName(groupName);
        task.start();
        return task;
    }

    public Map<String, List<List<String>>> getMsgList() {
        return messageData.getMsgList();
    }
    public webSocketMsg getWSM(String groupId){
        return groupWebSocketMsg.get(groupId);
    }
    public void removeSession(WebSocketSession session){
        for(webSocketMsg item:groupWebSocketMsg.values())
            item.removeSession(session);
    }

    public void saveToMysqlSignal(){
        mainSignal.setAll(true);
    }

    public Map<String, Boolean> getAllThreadStatus(){
        Map<String, Boolean> result = new HashMap<>();
        for(Integer groupId:groupThreads.keySet())
            result.put(groupId.toString(), groupThreads.get(groupId).isAlive());
//            result.put(groupId.toString(), String.valueOf(groupThreads.get(groupId).isAlive()));
        return result;
    }

    public void removeDeadThread(Integer groupId){
        groupThreads.remove(groupId);
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
    private final webSocketMsg wsm;

    Logger logger = LoggerFactory.getLogger(getClass());

    public groupMain(Integer limit,
                     Integer groupId,
                     ConcurrentLinkedQueue<List<String>> groupQueue,
                     List<List<String>> groupList,
                     groupMessageService gms,
                     manageSignal mySignal,
                     String env,
                     webSocketMsg wsm){
        this.limit = limit;
        this.groupId = groupId;
        this.groupList = groupList;
        this.groupQueue = groupQueue;
        this.gms = gms;
        this.mySignal = mySignal;
        this.env = env;
        this.wsm = wsm;
    }

    @Override
    public void run(){
        long recordTime = new Date().getTime();
        boolean Repeater = true;
        int num;
        int count = 0;
        boolean isExist;
        String recallOperator, messageId, userId, rawMessage, imageUrl, createDate, reply, userName, at;
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
            userName = sample.get(7);
            at = sample.get(8);

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
                List<String> sendRecall = new ArrayList<>();
                sendRecall.add(recallOperator);
                sendRecall.add(messageId);
                wsm.put(sendRecall);
                continue;
            }

            List<String> tmpMsg = new ArrayList<>();
            tmpMsg.add(recallOperator);
            tmpMsg.add(messageId);
            tmpMsg.add(userId);
            tmpMsg.add(rawMessage);
            tmpMsg.add(imageUrl);
            tmpMsg.add(createDate);
            tmpMsg.add(userName);

            JSONObject configTmp = GetConfig.config(env);
            List<String> ats = Arrays.asList(at.split(","));
            if(configTmp!=null && ats.contains(configTmp.getString("bot_qq"))){
                // 响应式chatbot
                String reMessage = "";
                botApi bot = new botApi(configTmp.getString("BOTROOT"));
                if(rawMessage.equals("") && !imageUrl.equals("")){
                    // 仅图片
                    reMessage = "在下双眼已瞎，看不到图";
                }else if(rawMessage.equals("") && imageUrl.equals("")){
                    // 全空
                    reMessage = "什么事？";
                }else{
                    // 仅文字or文字+图片，触发生成式chatbot
                    String copyMessage = rawMessage;
                    JSONObject chatbotConfig = configTmp.getJSONObject("chatbot").getJSONObject(groupId.toString());
                    Pattern startSpacePattern = Pattern.compile("^ +");
                    Pattern endSpacePattern = Pattern.compile(" +$");
                    Matcher startSpace = startSpacePattern.matcher(copyMessage);
                    if(startSpace.find())
                        copyMessage = copyMessage.substring(startSpace.end());
                    Matcher endSpace = endSpacePattern.matcher(copyMessage);
                    if(endSpace.find())
                        copyMessage = copyMessage.substring(0, endSpace.start());
                    JSONObject chat = bot.getChatbot(copyMessage);
                    logger.info(String.format("chatbot: %s", chat.getString("message")));
                    reMessage = String.format(chatbotConfig.getString("msgTemplate"), chat.getString("message"));
                }
                bot.sendGroupMsg(groupId.toString(), reMessage);
            }

            if(!reply.equals("") && num > 0){
                isExist = false;
                for(int n=num-1; n>Math.max(num-30, -1); n--){
                    if(groupList.get(n).get(1).equals(reply)){
                        isExist = true;
                        groupList.add(n+1, tmpMsg);
                        wsm.put(tmpMsg);
                        break;
                    }
                }
                if(!isExist){
                    groupList.add(tmpMsg);
                    wsm.put(tmpMsg);
                }
            }else{
                String lastMsg = null;
                if(groupList.size()>0)
                    lastMsg = groupList.get(groupList.size() - 1).get(3);


                JSONObject configJson;
                File file = new File(envGet.getByEnv(env) + "config/config.json");
                try{
                    String data = FileUtils.readFileToString(file);
                    configJson = JSON.parseObject(data);
                }catch(IOException e){
                    logger.error("读取配置文件出错，取消复读: ", e);
                    continue;
                }

                botApi bot = new botApi(configJson.getString("BOTROOT"));
                boolean same = isRepeate(rawMessage, configJson);
                // 添加进缓存
                groupList.add(tmpMsg);
                wsm.put(tmpMsg);

                // 复读机 || 打断鸡
                if((!rawMessage.equals("")) && same && Repeater){
                    Repeater = false;
                    String sendMessage;
                    String machine;
                    JSONObject groupRepeater = configJson.getJSONObject("Repeater").getJSONObject(groupId.toString());
                    if(groupRepeater.getBoolean("interOrRepeat")){
                        JSONArray interruptMsgs = groupRepeater.getJSONArray("interruptMsg");
                        sendMessage = interruptMsgs.getString((int) (Math.random() * interruptMsgs.size()));
                        machine = "打断鸡";
                    }
                    else{
                        sendMessage = rawMessage;
                        machine = "复读鸡";
                    }

//                    String url = String.format("http://127.0.0.1:5700/send_group_msg?group_id=%d&message=%s", groupId, sendMessage);
//                    Response res = post(url);
                    Response res = bot.sendGroupMsg(groupId.toString(), sendMessage);
                    System.out.println("\033[1;31m" +
                            String.join("", Collections.nCopies(40, "-")) +
                            String.format("%s启动：%d|%d|%s", machine, res.statusCode(), groupId, sendMessage) +
                            String.join("", Collections.nCopies(40, "-")) +
                            "\033[0m");
                }else if(!rawMessage.equals(lastMsg))
                    Repeater = true;

                count ++;
                if(!rawMessage.equals("")){
                    JSONObject chatbotConfig = configJson.getJSONObject("chatbot").getJSONObject(groupId.toString());
                    if(chatbotConfig != null && count >= chatbotConfig.getInteger("TriggerTime")){
                        // 调用生成式chatbot
                        JSONObject chat = bot.getChatbot(rawMessage);
                        logger.info(String.format("chatbot: %s", chat.getString("message")));
                        bot.sendGroupMsg(groupId.toString(), String.format(chatbotConfig.getString("msgTemplate"), chat.getString("message")));
                        count = 0;
                    }
                }
            }
        }
    }

    private boolean isRepeate(String rawMessage, JSONObject configJson){
        if(rawMessage.contains("我") && rawMessage.contains("傻"))
            return false;
        JSONObject ReGroup = configJson.getJSONObject("Repeater").getJSONObject(groupId.toString());
        if(ReGroup == null)
            return false;
        Integer num = ReGroup.getInteger("time");
        num = num - 1;
        if(groupList.size() < num)
            return false;
        for(int i=0; i<num; i++){
            if(!groupList.get(groupList.size() - 1 - i).get(3).equals(rawMessage))
                return false;
        }
        return true;
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