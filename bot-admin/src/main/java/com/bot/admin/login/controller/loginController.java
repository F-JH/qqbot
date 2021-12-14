package com.bot.admin.login.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class loginController {
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
}
