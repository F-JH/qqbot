package com.bot.admin.config;

import com.bot.admin.login.beans.RespBean;
import com.bot.admin.login.config.LoginFilter;
import com.bot.admin.login.config.MyAccessDeniedHandler;
import com.bot.admin.login.mybatis.dao.admin.RoleDao;
import com.bot.admin.login.mybatis.dao.admin.UserDao;
import com.bot.admin.login.mybatis.impl.UserService;
import com.bot.admin.login.mybatis.module.Role;
import com.bot.admin.login.mybatis.module.manager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserService userService;

    @Autowired
    RoleDao roleDao;

    @Autowired
    UserDao userDao;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    MyAccessDeniedHandler myAccessDeniedHandler;

    @Autowired
    MyAuthenticationEntryPoint myAuthenticationEntryPoint;

    @Bean
    PasswordEncoder passwordEncoder() {
//        return NoOpPasswordEncoder.getInstance();
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

    @Override
    public void configure(WebSecurity web) throws Exception{
        web.ignoring().antMatchers("/js/**", "/css/**","/images/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.authorizeRequests()
                    .antMatchers("/admin/**").hasRole("admin")
                    .antMatchers("/user/**").hasRole("user")
                    .anyRequest().authenticated()
                    .and()
                .exceptionHandling()
                    .accessDeniedHandler(myAccessDeniedHandler)
                    .and()
                .formLogin()
                    .defaultSuccessUrl("/hello")
                    .permitAll()
                    .and()
                .rememberMe()
                    .key("qqbotAdmin")
                    .and()
                .csrf()
                    .disable();
        http.addFilterAt(loginFilter(), UsernamePasswordAuthenticationFilter.class);
        // ??????????????????????????????????????????????????????????????????
        http.exceptionHandling()
                .authenticationEntryPoint(myAuthenticationEntryPoint);
    }

    @Bean
    RoleHierarchy roleHierarchy() {
        // ????????????
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("ROLE_admin > ROLE_user");
        hierarchy.setHierarchy("ROLE_admin > ROLE_enginer");
        hierarchy.setHierarchy("ROLE_enginer > ROLE_user");
        return hierarchy;
    }

    @Bean
    LoginFilter loginFilter()throws Exception{
        LoginFilter loginFilter = new LoginFilter();
        loginFilter.setAuthenticationSuccessHandler(new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                response.setContentType("application/json;charset=utf-8");
                PrintWriter out = response.getWriter();
                manager user =  (manager) authentication.getPrincipal();
                RespBean ok = RespBean.ok("????????????!", user);
                String s = new ObjectMapper().writeValueAsString(ok);
                out.write(s);
                out.flush();
                out.close();
            }
        });
        loginFilter.setAuthenticationFailureHandler(new AuthenticationFailureHandler() {
            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                response.setContentType("application/json;charset=utf-8");
                PrintWriter out = response.getWriter();
                RespBean respBean = RespBean.error(exception.getMessage());
                if (exception instanceof LockedException) {
                    respBean.setMsg("????????????????????????????????????!");
                } else if (exception instanceof CredentialsExpiredException) {
                    respBean.setMsg("?????????????????????????????????!");
                } else if (exception instanceof AccountExpiredException) {
                    respBean.setMsg("?????????????????????????????????!");
                } else if (exception instanceof DisabledException) {
                    respBean.setMsg("????????????????????????????????????!");
                } else if (exception instanceof BadCredentialsException) {
                    respBean.setMsg("???????????????????????????????????????????????????!");
                }
                out.write(new ObjectMapper().writeValueAsString(respBean));
                out.flush();
                out.close();
            }
        });
        loginFilter.setAuthenticationManager(authenticationManagerBean());
        loginFilter.setFilterProcessesUrl("/session");

        //TokenBasedRememberMeServices ?????????remember-me??????????????? getParameter ????????????????????????TokenBasedRememberMeServices????????????????????????url??????????????? ?remember-me=on
        TokenBasedRememberMeServices tokenBasedRememberMeServices = new TokenBasedRememberMeServices("qqbotAdmin", userService);
//        tokenBasedRememberMeServices.setTokenValiditySeconds(172800); // ?????????????????????
        tokenBasedRememberMeServices.setTokenValiditySeconds(30);
        loginFilter.setRememberMeServices(tokenBasedRememberMeServices);
//        loginFilter.doFilter();
        return loginFilter;
    }

    @Override
    @Bean
    protected UserDetailsService userDetailsService(){
        if(!userService.userExists("fujuhong")){
            contextLoads();
        }
        return userService;
    }

    void contextLoads(){
        System.out.println("-----------------------------????????????????????????-----------------------------");
        List<Role> roles = new ArrayList<>();
        Role role1 = new Role("ROLE_admin", "?????????");
        Role role2 = new Role("ROLE_enginer", "??????");
        Role role3 = new Role("ROLE_user", "????????????");
        roles.add(role1);
        roles.add(role2);
        roles.add(role3);
        roleDao.insertRoles(roles);

        System.out.println("-----------------------------????????????????????????-----------------------------");

        manager u1 = new manager();
        u1.setUsername("fujuhong");
        u1.setPassword(encoder.encode("972583048"));
        u1.setNickname("??????");
        u1.setAccountNonExpired(true);
        u1.setAccountNonLocked(true);
        u1.setCredentialsNonExpired(true);
        u1.setEnabled(true);
        List<Role> rs1 = new ArrayList<>();
        Role r1 = new Role();
        r1.setName("ROLE_admin");
        r1.setNameZh("?????????");
        rs1.add(r1);
        Role r12 = new Role();
        r12.setName("ROLE_enginer");
        r12.setNameZh("??????");
        rs1.add(r12);
        u1.setRoles(rs1);
        try{
            userDao.createUser(u1);
            userDao.setUserRole(u1);
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }


        manager u2 = new manager();
        u2.setUsername("yangyue");
        u2.setPassword(encoder.encode("fujuhong"));
        u2.setNickname("??????");
        u2.setAccountNonExpired(true);
        u2.setAccountNonLocked(true);
        u2.setCredentialsNonExpired(true);
        u2.setEnabled(true);
        List<Role> rs2 = new ArrayList<>();
        Role r2 = new Role();
        r2.setName("ROLE_user");
        r2.setNameZh("????????????");
        rs2.add(r2);
        u2.setRoles(rs2);
        try{
            userDao.createUser(u2);
            userDao.setUserRole(u2);
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}