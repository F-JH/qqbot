create database qqbot if not exists `qqbot`;

create table if not exists `group_message`(
    group_id char(15) not null,
    recall_operator char(15),
    message_id char(20) not null,
    index(message_id),
    qq char(15) not null,
    index(qq),
    raw_message varchar(50),
    image_url varchar(1024),
    create_date datetime not null
);