package com.bot.admin.config;

import com.bot.admin.login.beans.RespBean;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Component
public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {
    // 没有认证或认证过期失效时在这里处理，不重定向
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json;charset=utf-8");
        httpServletResponse.setStatus(200);
        PrintWriter out = httpServletResponse.getWriter();
        System.out.println(e.toString());
        RespBean respBean = RespBean.error(e.getMessage());
        respBean.setCode(1001);

        Map<String, String> moreinfo = new HashMap<>();
        moreinfo.put("location", "/login");
        respBean.setObj(moreinfo);

        out.write(new ObjectMapper().writeValueAsString(respBean));
        out.flush();
        out.close();
    }
}
