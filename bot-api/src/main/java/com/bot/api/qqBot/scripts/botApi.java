package com.bot.api.qqBot.scripts;

import io.restassured.response.Response;

import static io.restassured.RestAssured.*;

public class botApi {
    private final String botRoot;
    private static final String groupUrl = "/send_group_msg";

    public botApi(String botRoot){
        this.botRoot = botRoot;
    }

    public Response sendGroupMsg(String groupId, String message){
        // 原生态发送
        return post(botRoot + groupUrl + String.format("?group_id=%s&message=%s", groupId, message));
    }

    public Response sendAt(String groupId, String message, String userId){
        // @某人
        message += String.format("[CQ:at,qq=%s]", userId);
        return sendGroupMsg(groupId, message);
    }

    public Response sendGroupImage(String groupId, String filepath){
        // 仅发送图片
        String message = String.format("[CQ:image,file=http://127.0.0.1:8080/%s]", filepath);
        return sendGroupMsg(groupId, message);
    }
}
