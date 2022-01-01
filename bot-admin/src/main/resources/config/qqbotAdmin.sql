use qqbotAdmin;

--用户表
create table if not exists users(
    id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    username varchar(50) not null,
    password varchar(500) not null,
    nickname varchar(50),
    account_non_expired bool not null,
    account_non_locked bool not null,
    credentials_non_expired bool not null,
    enabled boolean not null
);
--create table if not exists authorities(
--    user_id int unsigned not null,
----    username varchar(50) not null,
--    authority varchar(50) not null,
--    constraint fk_authorities_users foreign key(user_id) references users(id)
--);
create table if not exists role(
    id INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    role_name varchar(50) not null,
    role_name_zh varchar(50)
);
create table if not exists user_role(
    user_id int unsigned not null,
    role_id int unsigned not null,
    primary key(user_id, role_id)
);

--create unique index ix_auth_username on authorities (username,authority);

--删除表
--drop table user_role, role, authorities, users;
--清空表
delete from role;
delete from user_role;
delete from users;

--初始化role
insert into role(role_name, role_name_zh) values('ROLE_admin', '管理员'), ('ROLE_user', '普通用户');
--初始话users
insert into
    users(username, password, nickname, account_non_expired, account_non_locked, credentials_non_expired, enabled)
values
    ('fujuhong', '972583048', '半夏', 1, 1, 1, 1),
    ('yangyue', 'fujuhong', '杨越', 1, 1, 1, 1),
    ('test', '123', '测试', 1, 1, 1, 1);
--初始化user_role
insert into
    user_role(user_id, role_id)
select users.id user_id, role.id role_id from users join role on users.id where username = 'fujuhong' and role_name in ('ROLE_admin','ROLE_user');


--配置表
create table focus_group(
    id bigint UNSIGNED NOT NULL PRIMARY KEY comment '群号',
    groupName varchar(50) comment '群组名',
    is_print boolean not null comment '是否在日志上打印群消息',
    is_flush boolean not null comment '是否每日刷新群名'
);

create table focus_recall(
    group_id bigint UNSIGNED NOT NULL comment '群号',
    focus_user bigint UNSIGNED not null comment '关注的用户'
);

create table focus_message_type(
    post_type varchar(20) not null,
    notice_type varchar(20)
);

create table welcom_group(
    group_id bigint unsigned not null comment '群号',
    parliamentary bigint unsigned not null comment '议员',
    keyword varchar(20) comment '关键词',
    pattern varchar(50) comment '正则匹配'
);

create table welcome_message(
    group_id bigint unsigned not null comment '群号',
    raw_message varchar(50) comment '文本消息，欢迎语',
    image varchar(2048) comment '图片的url地址'
)comment='欢迎消息';

create table repeater(
    group_id bigint unsigned not null PRIMARY KEY comment '群号',
    trigger_time int(2) comment '触发次数',
    is_repeater boolean not null comment '是否复读',
    is_interrupt boolean not null comment '是否打断（触发优先级低于复读）',
    repeater_message varchar(50) comment '复读消息，留空则发送原始复读消息'
)comment='复读机/打断鸡配置';

create table interrupt(
    group_id bigint unsigned not null comment '群号',
    is_use boolean not null comment '是否生效',
    interrupt_message varchar(50)
)comment='打断鸡消息配置';

create table member_leave(
    group_id bigint unsigned not null comment '群号',
    kick_temple varchar(50),
    leave_temple varchar(50)
)comment='离开群组消息';

--删除配置表
drop table
focus_group, focus_recall, focus_message_type, welcom_group, welcome_message, repeater, interrupt, member_leave;

--清空表
--delete from focus_group;
--delete from focus_recall;
--delete from focus_message_type;
--delete from welcom_group;
--delete from welcome_message;
--delete from repeater;
--delete from interrupt;
--delete from member_leave;

--初始化
