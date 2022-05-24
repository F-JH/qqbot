package com.bot.server.qqBot.Beans;

import org.springframework.stereotype.Component;

@Component
public class MessageProcessQueue {
    private static class test{
        public void fujuhong(){
            System.out.println("fujuhong");
        }
    }
    public void yangyue(){
        test a = new test();
        a.fujuhong();
        System.out.println("yangyue");
    }
}
