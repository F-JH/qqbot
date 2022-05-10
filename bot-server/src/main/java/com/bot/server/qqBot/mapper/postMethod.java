package com.bot.server.qqBot.mapper;

import com.alibaba.fastjson.JSONObject;

public interface postMethod{
    public String run(JSONObject msg, JSONObject configJson, Integer groupId);
}