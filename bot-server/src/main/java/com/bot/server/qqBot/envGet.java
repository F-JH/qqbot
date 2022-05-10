package com.bot.server.qqBot;

public class envGet {
    // 配置不同环境的配置文件地址
    private static final String testEnv = "bot-server/src/main/resources/";
    private static final String prodEnv = "qqbot/bot-server/src/main/resources/";
    private boolean isModify;

    public static String getByEnv(String envName) {
        if (envName.equals("test"))
            return testEnv;
        else if (envName.equals("prod"))
            return prodEnv;
        else
            return null;
    }
}
