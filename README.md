# qqbot
SpringBoot写的qqbot

计划是一个多模块的项目，目前只完成了bot-api

环境：Java 11、Maven

打包：

`mvn clean`  
`mvn package`  
生成jar文件：   
`bot-api/target/bot-api-xxx.jar`  
`bot-admin/target/bot-admin-xxx.jar (还没弄好)`

## bot-api

配置文件:

    bot-api/src/main/resources/下的三个application yml文件;
    其中 dev版本是测试环境，prod版本是生产环境
    Spring.datasource: 自行设置mysql配置
    注意：prod版的logging.config需要自行修改，根据自己的路径来，定位到resources/cofnig/logback.xml
        或者你把它去掉也行




运行(建议在根目录下运行)：   
`java -jar bot-api/target/bot-api-xxx.jar --spring.profiles.active=dev --env=test`  
    
    spring.profiles.active: 使用那个环境的application.yml 
    env: 决定一个外部配置文件的路径，具体可以看 com.bot.api.qqBot.envGet