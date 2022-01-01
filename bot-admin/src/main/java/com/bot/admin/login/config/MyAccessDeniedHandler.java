package com.bot.admin.login.config;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class MyAccessDeniedHandler implements AccessDeniedHandler {

    // 无访问权限配置
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)throws IOException, ServletException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.getWriter().println("{\"code\":401,\"message\":\"小弟弟，你没有权限访问呀！\",\"data\":null}");
        response.getWriter().flush();
    }
}
