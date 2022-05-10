package com.bot.server.qqBot.Controllers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bot.server.qqBot.envGet;
import com.bot.server.qqBot.mapper.postMethod;
import com.bot.server.qqBot.server.msgManage;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;

import com.bot.server.qqBot.mybatis.service.groupMessageService;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Configuration
@RequestMapping("/message")
public class routeController{

    @Value("${env}")
    private String env;

    @Autowired
    private Map<String, postMethod> postMethodMap;

    @Autowired
    private msgManage manage;

    @Autowired
    private groupMessageService groupMessageServiceMy;

    @RequestMapping(method = RequestMethod.POST)
    public String botApi(@RequestBody JSONObject msgJson){
//        System.out.println(RequestContextHolder.getRequestAttributes().toString());
//        File file = new File("resources/config/config.json");
        File file = new File(envGet.getByEnv(env) + "config/config.json");
        JSONObject configJson;
        try{
            String data = FileUtils.readFileToString(file);
            configJson = JSON.parseObject(data);
        }catch(IOException e){
            throw new RuntimeException(e);
        }
        Integer groupId = msgJson.getInteger("group_id");
        if (groupId == null)
            return "not find group id!";
        JSONObject focusGroup = configJson.getJSONObject("focusGroup");

        if(focusGroup.containsKey(groupId.toString())){
            if(groupId.equals(961530103))
                System.out.println("Debug");        // 私人测试组 debug 专用
            String postType = msgJson.getString("post_type");
            if(postType == null)
                return "not find post type";
            if(configJson.getJSONArray("post_type").contains(postType)){
                postMethod method = postMethodMap.get(postType);
                if(method != null)
                    return method.run(msgJson, configJson, groupId);
            }
        }
        return "OK";
    }

    @RequestMapping(value = "/show-list")
    public String showList(){
        Map<String, List<List<String>>> msgList = manage.getMsgList();
        return JSONObject.toJSONString(msgList);
    }

    @RequestMapping(value = "/saveToMysql")
    public boolean saveToMysql(){
        manage.saveToMysqlSignal();
        return true;
    }

    @RequestMapping(value = "/checkThread")
    public Map<String, String> checkThread(String groupId) throws NoSuchMethodException {
        if (groupId == null){
            Map<String, Boolean> checkResult = manage.getAllThreadStatus();
            Map<String, String> result = new HashMap<>();
            for(String key:checkResult.keySet()){
                result.put(key, checkResult.get(key).toString());
            }
            return result;
        }
        else
            return manage.getGroupThreadStatus(groupId);
    }

    @RequestMapping(value = "/show-envs")
    public Map<String, String> showEnvs(){
        Map<String, String> result = new HashMap<>();
        result.put("test", envGet.getByEnv("test"));
        result.put("prod", envGet.getByEnv("prod"));
        return result;
    }

    @RequestMapping(value = "/test")
    public String test(String id){
        String key = "test";
        System.out.println(this);
        System.out.println(id);
        System.out.println(key);
        return "";
    }

//    @RequestMapping(value = "/create")
//    public Integer create(String groupId){
//        return groupMessageServiceMy.createTable(groupId);
//    }
//
//    @RequestMapping(value = "/testJson", method = RequestMethod.POST)
//    public String testJson(@RequestBody JSONObject json){
//        String s = json.getString("rawMessage");
//        return s;
//    }
}
