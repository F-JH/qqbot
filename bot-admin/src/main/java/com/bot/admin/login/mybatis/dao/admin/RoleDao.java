package com.bot.admin.login.mybatis.dao.admin;

import com.bot.admin.login.mybatis.module.Role;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface RoleDao {
    @Insert({
            "<script>",
            "insert into role(role_name, role_name_zh) values",
            "<foreach collection='roles' item='role' index='index' separator=','>",
            "(#{role.name}, #{role.nameZh})",
            "</foreach>",
            "</script>"
    })
    public int insertRoles(@Param("roles") List<Role> roles);
}
