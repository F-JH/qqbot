package com.bot.admin.login.mybatis.module;

import java.io.Serializable;
import java.util.List;

public class Role implements Serializable {
    private Integer id;

    private String name;

    private String nameZh;

    public Role(){}

    public Role(String name, String nameZh){
        this.name = name;
        this.nameZh = nameZh;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getNameZh() {
        return nameZh;
    }

    public void setNameZh(String nameZh) {
        this.nameZh = nameZh;
    }
}