package com.bot.admin.login.mybatis.impl;

import com.bot.admin.login.mybatis.dao.admin.UserDao;
import com.bot.admin.login.mybatis.module.Role;
import com.bot.admin.login.mybatis.module.manager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        manager user = userDao.findUserByUsername(username);
        if(user == null){
            throw new UsernameNotFoundException("用户不存在!");
        }
        try{
            Integer user_id = userDao.findUserIdByUsername(user.getUsername());
            List<Role> roles = userDao.findRolesByUserId(user_id);
            user.setRoles(roles);
        }catch (SQLException e){}
        return user;
    }

    public boolean userExists(String username){
        UserDetails user = userDao.findUserByUsername(username);
        if(user == null){
            return false;
        }
        return true;
    }
}
