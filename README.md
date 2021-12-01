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

外部配置：  

    这是一个外部配置文件，这意味着你可以动态修改它，实时生效
    配置文件路径：bot-api/src/resources/config/config.json
    详细说明：
        focusGroup：关注的群号，只有这些群消息才会被处理，key(群号)，vlaue(群名，自己填)
        recall：撤回消息，key(群号),value(一个列表，表示关注的执行撤回对象是谁，qq号，用于标记是否是涉黄/垃圾消息)
        post_type: 关注的消息类型，一般不用改
        notice_type: 关注的notice type，一般不用改
        pringGroup: 这个是日志打印的时候会打印那些群消息
        welcome_group：哪些群会发送欢迎新成员消息，消息比较固定，就一张图，且只能在focusGroup里面选群
            value的列表：表示长老会成员，简单说就是只有这里面的某个人发送了带有"欢迎"的消息，机器人才会发欢迎图，如果是空列表，那么就表示议会通过，直接发
            image：可以根据群来筛选发哪些图，图的路径在 bot-api/src/resources/static 下，注意打包后它读的是jar包里的图，你在外面增加没用
        Repeater：复读机配置，key(群号)，value(消息重复几次会触发复读机)、
        BOTROOT：go-cqhttp 发消息监听地址
        imgUrlTemple：这个是qqbot_web的内容，不要改