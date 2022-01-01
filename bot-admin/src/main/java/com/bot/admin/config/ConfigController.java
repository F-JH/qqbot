package com.bot.admin.config;

import com.alibaba.fastjson.JSONObject;
import com.bot.admin.config.method.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class ConfigController {

    @Autowired
    EnvGet envGet;

    @RequestMapping(value = "/config", method = RequestMethod.POST)
    public JSONObject config(){
        return Config.getNavigation(envGet);
    }
}
