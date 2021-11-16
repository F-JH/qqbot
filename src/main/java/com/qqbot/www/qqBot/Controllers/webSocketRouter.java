package com.qqbot.www.qqBot.Controllers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.qqbot.www.qqBot.Beans.webSocketBean;

@Configuration
@EnableWebSocket
public class webSocketRouter implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry){
        registry.addHandler(wsHandler(), "/ws_message")
                .setAllowedOrigins("*");
    }

    @Bean
    public WebSocketHandler wsHandler(){
        return new webSocketBean();
    }
}
