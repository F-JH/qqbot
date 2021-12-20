package com.bot.admin.login.beans;

public class RespBean {
    private Integer status;
    private String msg;
    private Object userInfo;

    public static RespBean build() {
        return new RespBean();
    }

    public static RespBean ok(String msg) {
        return new RespBean(200, msg, null);
    }

    public static RespBean ok(String msg, Object userInfo) {
        return new RespBean(200, msg, userInfo);
    }

    public static RespBean error(String msg) {
        return new RespBean(500, msg, null);
    }

    public static RespBean error(String msg, Object userInfo) {
        return new RespBean(500, msg, userInfo);
    }

    private RespBean() {
    }

    private RespBean(Integer status, String msg, Object userInfo) {
        this.status = status;
        this.msg = msg;
        this.userInfo = userInfo;
    }

    public Integer getStatus() {
        return status;
    }

    public RespBean setStatus(Integer status) {
        this.status = status;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public RespBean setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public Object getUserInfo() {
        return userInfo;
    }

    public RespBean setObj(Object userInfo) {
        this.userInfo = userInfo;
        return this;
    }
}