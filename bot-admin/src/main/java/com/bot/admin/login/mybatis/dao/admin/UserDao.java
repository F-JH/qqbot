package com.bot.admin.login.mybatis.dao.admin;

import com.bot.admin.login.mybatis.module.Role;
import com.bot.admin.login.mybatis.module.manager;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;

import java.sql.SQLException;
import java.util.List;

public interface UserDao {

    @Results(id = "findUserByUsername", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "username", column = "username"),
            @Result(property = "password", column = "password"),
            @Result(property = "nickname", column = "nickname"),
            @Result(property = "accountNonExpired", column = "account_non_expired"),
            @Result(property = "accountNonLocked", column = "account_non_locked"),
            @Result(property = "credentialsNonExpired", column = "credentials_non_expired"),
            @Result(property = "enabled", column = "enabled")
    })
    @Select({
            "select ",
                "id, nickname, username, password, account_non_expired, account_non_locked, credentials_non_expired, enabled",
            "from",
                "users",
            "where",
                "username = #{username}"
    })
    public manager findUserByUsername(@Param("username") String username);

    // 找user_id
    @Select({
            "select id from users where username = #{username}"
    })
    public Integer findUserIdByUsername(@Param("username") String username) throws SQLException;

    @Select({
            "select authority from authorities where user_id = ${user_id}"
    })
    public String findAuthByUserid(@Param("user_id")  Integer user_id) throws SQLException;

    // 找role
    @Results(id="findRoleByUserId", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "name", column = "role_name"),
            @Result(property = "nameZh", column = "role_name_zh")
    })
    @Select({
            "select id, role_name, role_name_zh from user_role join role on role_id = id where user_id = ${user_id}"
    })
    public List<Role> findRolesByUserId(@Param("user_id") Integer user_id) throws SQLException;

    @Insert({
            "insert into users",
            "(username, password, nickname, account_non_expired, account_non_locked, credentials_non_expired, enabled)",
            "values(#{user.username}, #{user.password}, #{user.nickname}, ${user.accountNonExpired}, ${user.accountNonLocked}, ${user.credentialsNonExpired}, ${user.enabled})"
    })
    public int createUser(@Param("user") manager user) throws SQLException;

    // 设置用户角色
    @Insert({
            "<script>",
            "Insert into user_role(user_id, role_id)",
            "select users.id user_id, role.id role_id from users join role on users.id where username = 'fujuhong' and role_name in(",
            "<foreach collection='user.roles' item='role' index='index' separator=','>",
            "#{role.name}",
            "</foreach>",
            ")",
            "</script>",
    })
    public int setUserRole(@Param("user") manager user) throws SQLException;
}
