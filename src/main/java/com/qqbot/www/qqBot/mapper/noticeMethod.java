package com.qqbot.www.qqBot.mapper;

import com.alibaba.fastjson.JSONObject;
import com.qqbot.www.qqBot.server.msgManage;

public interface noticeMethod {
    public String run(JSONObject msg, JSONObject configJson, Integer groupId, msgManage manage);
}