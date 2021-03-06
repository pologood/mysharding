drop table if exists sys_user_1;
drop table if exists sys_user_2;

drop table if exists sys_role_1;
drop table if exists sys_role_2;
drop table if exists sys_role_3;
drop table if exists sys_role_4;

drop table if exists sys_task_a;

create table sys_user_1 (
  user_id bigint generated by default as identity,
  username varchar(100),
  password varchar(100),
  salt varchar(100),
  primary key (user_id)
);
create table sys_user_2 (
  user_id bigint generated by default as identity,
  username varchar(100),
  password varchar(100),
  salt varchar(100),
  primary key (user_id)
);
create table sys_role_1 (
  role_id bigint generated by default as identity,
  user_id bigint,
  role_name varchar(100),
  primary key (role_id)
);
create table sys_role_2 (
  role_id bigint generated by default as identity,
  user_id bigint,
  role_name varchar(100),
  primary key (role_id)
);
create table sys_role_3 (
  role_id bigint generated by default as identity,
  user_id bigint,
  role_name varchar(100),
  primary key (role_id)
);
create table sys_role_4 (
  role_id bigint generated by default as identity,
  user_id bigint,
  role_name varchar(100),
  primary key (role_id)
);

create table sys_task_a (
  task_id bigint generated by default as identity,
  user_id bigint,
  task_name varchar(100),
  primary key (task_id)
);