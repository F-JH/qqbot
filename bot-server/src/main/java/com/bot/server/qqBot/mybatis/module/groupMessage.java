package com.bot.server.qqBot.mybatis.module;

import java.util.Date;

public class groupMessage {
    private String recall_operator;
    private String message_id;
    private String qq;
    private String raw_message;
    private String image_url;
    private Date create_date;

    public groupMessage(){}

    public groupMessage(String recall_operator, String message_id,
                        String qq, String raw_message, String image_url, Date create_date){
        this.recall_operator = recall_operator;
        this.message_id = message_id;
        this.qq = qq;
        this.raw_message = raw_message;
        this.image_url = image_url;
        this.create_date = create_date;
    }

    public String getRecall_operator() {
        return recall_operator;
    }

    public void setRecall_operator(String recall_operator) {
        this.recall_operator = recall_operator;
    }

    public String getMessage_id() {
        return message_id;
    }

    public void setMessage_id(String message_id) {
        this.message_id = message_id;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getRaw_message() {
        return raw_message;
    }

    public void setRaw_message(String raw_message) {
        this.raw_message = raw_message;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public Date getCreate_date() {
        return create_date;
    }

    public void setCreate_date(Date create_date) {
        this.create_date = create_date;
    }
}
