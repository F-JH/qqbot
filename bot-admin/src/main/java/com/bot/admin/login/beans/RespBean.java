package com.bot.admin.login.beans;

public class RespBean {
    private Integer code;
    private String msg;
    private Object moreInfo;

    public static RespBean build() {
        return new RespBean();
    }

    public static RespBean ok(String msg) {
        return new RespBean(200, msg, null);
    }

    public static RespBean ok(String msg, Object moreInfo) {
        return new RespBean(200, msg, moreInfo);
    }

    public static RespBean error(String msg) {
        return new RespBean(500, msg, null);
    }

    public static RespBean error(String msg, Object moreInfo) {
        return new RespBean(500, msg, moreInfo);
    }

    private RespBean() {
    }

    private RespBean(Integer code, String msg, Object moreInfo) {
        this.code = code;
        this.msg = msg;
        this.moreInfo = moreInfo;
    }

    public Integer getCode() {
        return code;
    }

    public RespBean setCode(Integer code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public RespBean setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public Object getMoreInfo() {
        return moreInfo;
    }

    public RespBean setObj(Object moreInfo) {
        this.moreInfo = moreInfo;
        return this;
    }
}