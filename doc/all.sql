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