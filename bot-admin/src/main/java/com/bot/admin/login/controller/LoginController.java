package com.bot.admin.login.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class LoginController {
    @RequestMapping("/hello")
    public String login(){
        return "login";
    }

    @RequestMapping("/index")
    public String love(){
        return "fujuhong love yangyue";
    }

    @RequestMapping("/admin/hello")
    public String admin(){
        return "admin";
    }

    @RequestMapping("/user/hello")
    public String user(){
        return "user";
    }

    @RequestMapping("/getIndex")
    public JSONObject menu(){
        String menuString = "{\"code\":200,\"resultData\":{\"撤回消息\":{\"class\":\"sub-menu\",\"icon\":\"MessageOutlined\",\"children\":{\"群组设置\":{\"icon\":\"\",\"path\":\"/group\"}}}}}";
        return JSON.parseObject(menuString);
    }
}
