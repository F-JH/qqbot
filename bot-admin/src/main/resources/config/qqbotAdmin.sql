use qqbotAdmin;
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
