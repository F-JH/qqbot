package com.bot.api.qqBot.mapper;

import com.alibaba.fastjson.JSONObject;
import com.bot.api.qqBot.server.msgManage;

public interface noticeMethod {
    public String run(JSONObject msg, JSONObject configJson, Integer groupId, msgManage manage);
}