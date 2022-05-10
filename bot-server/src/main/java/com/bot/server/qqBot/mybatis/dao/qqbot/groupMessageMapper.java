package com.bot.server.qqBot.mybatis.dao.qqbot;

import org.apache.ibatis.annotations.*;
import com.bot.server.qqBot.mybatis.module.groupMessage;

import java.sql.SQLException;
import java.util.List;

public interface groupMessageMapper {
    @Results(id = "test", value = {
            @Result(property = "recall_operator", column = "recall_operator"),
            @Result(property = "message_id", column = "message_id"),
            @Result(property = "qq", column = "qq"),
            @Result(property = "raw_message", column = "raw_message"),
            @Result(property = "image_url", column = "image_url"),
            @Result(property = "create_date", column = "create_date")
    })
    @Select("select recall_operator, message_id, qq, raw_message, image_url, create_date from group_message${tableName}")
    public List<groupMessage> findAll(@Param("tableName") String name) throws SQLException;

    @Update({"create table if not exists group_message${tableName}(",
            "recall_operator char(15),",
            "message_id char(20) not null,",
            "index(message_id),",
            "qq char(15) not null,",
            "index(qq),",
            "raw_message varchar(50),",
            "image_url varchar(1024),",
            "create_date datetime not null)"
    })
    public int createTable(@Param("tableName") String name);

    @Insert({
            "<script>",
            "insert into group_message${tableName}(recall_operator, message_id, qq, raw_message, image_url, create_date) values",
            "<foreach collection='msgs' item='item' index='index' separator=','>",
            "(#{item.recall_operator}, #{item.message_id}, #{item.qq}, #{item.raw_message}, #{item.image_url}, #{item.create_date})",
            "</foreach>",
            "</script>"
    })
    public int add(@Param("tableName") String tableName, @Param("msgs") List<groupMessage> msgs) throws SQLException;

    @Update({
            "update group_message${tableName} set recall_operator=#{recallOperator} where message_id=#{messageId}"
    })
    public int setRecall(@Param("tableName") String tableName, @Param("recallOperator") String recallOperator, @Param("messageId") String messageId) throws SQLException;
}
