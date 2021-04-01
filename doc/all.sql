# 电子书表
drop table if exists `ebook`;
create table `ebook` (
                         `id` bigint not null comment 'id',
                         `name` varchar(50) comment '名稱',
                         `category1_id` bigint comment '分類1',
                         `category2_id` bigint comment '分類2',
                         `description` varchar(200) comment '描述',
                         `cover` varchar(200) comment '封面',
                         `doc_count` int not null default 0 comment '文檔數',
                         `view_count` int not null default 0 comment '閱讀數',
                         `vote_count` int not null default 0 comment '點讚數',
                         primary key (`id`)
) engine=innodb default charset=utf8mb4 comment='電子書';

insert into `ebook` (id, name, description) values (1, 'Spring Boot 入門課程', '零基礎入門 Java 開發，企業級應用開發最佳首選框架');
insert into `ebook` (id, name, description) values (2, 'Vue 入門課程', '零基礎入門 Vue 開發，企業級應用開發最佳首選框架');
insert into `ebook` (id, name, description) values (3, 'Python 入門課程', '零基礎入門 Python 開發，企業級應用開發最佳首選框架');
insert into `ebook` (id, name, description) values (4, 'Mysql 入門課程', '零基礎入門 Mysql 開發，企業級應用開發最佳首選框架');
insert into `ebook` (id, name, description) values (5, 'Oracle 入門課程', '零基礎入門 Oracle 開發，企業級應用開發最佳首選框架');

drop table if exists `test`;
create table `test`(
 `id` bigint not null comment 'id',
 `name` varchar(50) comment '名稱',
 `password` varchar(50) comment '密碼',
 primary key(`id`)
) engine=innodb default charset=utf8mb4 comment='測試';

insert into `test`(id,name,password) values (2,'測試2','password2');

drop table if exists `demo`;
create table `demo`(
 `id` bigint not null comment 'id',
 `name` varchar(50) comment '名稱',
 `password` varchar(50) comment '密碼',
 primary key(`id`)
) engine=innodb default charset=utf8mb4 comment='測試';

insert into `demo`(id,name,password) values (2,'測試2','password2');