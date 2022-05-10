package com.bot.server.qqBot.mapper;

import com.alibaba.fastjson.JSONObject;
import com.bot.server.qqBot.server.msgManage;

public interface noticeMethod {
    public String run(JSONObject msg, JSONObject configJson, Integer groupId, msgManage manage);
}