package com.qqbot.www.qqBot.mybatis.impl;

import com.qqbot.www.qqBot.mybatis.module.groupMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.qqbot.www.qqBot.mybatis.dao.groupMessageMapper;
import com.qqbot.www.qqBot.mybatis.service.groupMessageService;

import java.sql.SQLException;
import java.util.List;

@Service
public class groupMessageImpl implements groupMessageService {
    @Autowired
    private groupMessageMapper messageMapper;

    public List<groupMessage> findAll(String groupId) throws SQLException{
        if(!groupId.equals("")){
            groupId = "_" + groupId;
        }
        return messageMapper.findAll(groupId);
    }

    public int createTable(String groupId){
        if(!groupId.equals("")){
            groupId = "_" + groupId;
        }
        return messageMapper.createTable(groupId);
    }

    public int add(String groupId, List<groupMessage> msgs) throws SQLException{
        if(!groupId.equals("")){
            groupId = "_" + groupId;
        }
        return messageMapper.add(groupId, msgs);
    }

    public int setRecall(String groupId, String recallOperator, String messageId)throws SQLException{
        if(!groupId.equals("")){
            groupId = "_" + groupId;
        }
        return messageMapper.setRecall(groupId, recallOperator, messageId);
    }
}
