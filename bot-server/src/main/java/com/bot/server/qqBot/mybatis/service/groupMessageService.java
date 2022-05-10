package com.bot.server.qqBot.mybatis.service;

import com.bot.server.qqBot.mybatis.module.groupMessage;

import java.sql.SQLException;
import java.util.List;

public interface groupMessageService {
    public List<groupMessage> findAll(String name) throws SQLException;
    public int createTable(String name);
    public int add(String name, List<groupMessage> msgs) throws SQLException;
    public int setRecall(String name, String recallOperator, String messageId) throws SQLException;
}
