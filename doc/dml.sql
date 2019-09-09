/*
 * 初始化表
 * 【描述】初始化表
 */

--1、初始化系统用户表，用于admin-server登录
insert into t_sys_role(id,name,comment) values(1,'ROLE_ADMIN','管理员');
insert into t_sys_role(id,name,comment) values(2,'ROLE_OPS','运维人员');
insert into t_sys_role(id,name,comment) values(3,'ROLE_USER','普通用户');
insert into t_sys_user(nick,username,password,role_id) values('管理员','admin','0192023a7bbd73250516f069df18b500',1);

insert into t_tilemapservice(id,title,abstract,version,kind,href) values(1,'XYZ Map Service','A XYZ Tile Map Service','1.0.0',1,'http://${mapserver}/xyz/1.0.0/');
insert into t_tilemapservice(id,title,abstract,version,kind,href) values(2,'TMS Map Service','A TMS Tile Map Service','1.0.0',2,'http://${mapserver}/tms/1.0.0/');

--2、初始化服务应用
insert into ops.t_application(application,comment) values('lbs-server','瓦片地图引擎');
insert into ops.t_application(application,comment) values('lbi-web','数据门户');
insert into ops.t_application(application,comment) values('database-config','数据库配置');

--3、初始化服务应用配置项
--（1）、修改PG配置
insert into ops.t_application_properties(profile,key, value, comment, application) values('dev','spring.datasource.url','jdbc:postgresql://111.202.109.210:5432/cateye','数据源连接','database-config');
insert into ops.t_application_properties(profile,key, value, comment, application) values('dev','spring.datasource.username','cateye','数据源用户','database-config');
insert into ops.t_application_properties(profile,key, value, comment, application) values('dev','spring.datasource.password','#Cateye@2019$','数据源密码','database-config');

insert into ops.t_application_properties(profile,key, value, comment, application) values('prod','spring.datasource.url','jdbc:postgresql://172.25.28.116:5432/cateye','数据源连接','database-config');
insert into ops.t_application_properties(profile,key, value, comment, application) values('prod','spring.datasource.username','cateye','数据源用户','database-config');
insert into ops.t_application_properties(profile,key, value, comment, application) values('prod','spring.datasource.password','#Cateye@2019$','数据源密码','database-config');

--（2）、服务应用配置
--lbi-web
insert into ops.t_application_properties(profile,key, value, comment, application) values('default','server.port','8888','端口号','lbi-web');
insert into ops.t_application_properties(profile,key, value, comment, application) values('default','spring.table.t_log','t_log','API调用日志表','lbi-web');
insert into ops.t_application_properties(profile,key, value, comment, application) values('default','spring.table.t_project','t_project','项目表','lbi-web');
insert into ops.t_application_properties(profile,key, value, comment, application) values('default','spring.table.t_dataset','t_dataset','数据集表','lbi-web');
insert into ops.t_application_properties(profile,key, value, comment, application) values('default','spring.table.t_tilemap','t_tilemap','瓦片地图表','lbi-web');
insert into ops.t_application_properties(profile,key, value, comment, application) values('default','spring.table.t_tileset','t_tileset','瓦片数据集表','lbi-web');
insert into ops.t_application_properties(profile,key, value, comment, application) values('default','spring.table.t_sys_role','t_sys_role','系统角色表','lbi-web');
insert into ops.t_application_properties(profile,key, value, comment, application) values('default','spring.table.t_sys_user','t_sys_user','系统用户表','lbi-web');
--lbi-tile
insert into ops.t_application_properties(profile,key, value, comment, application) values('default','server.port','8080','端口号','lbs-server');

insert into ops.t_application_properties(profile,key, value, comment, application) values('dev','spring.table.t_sys_role','t_sys_role','系统角色表','lbs-server');
insert into ops.t_application_properties(profile,key, value, comment, application) values('dev','spring.table.t_sys_user','t_sys_user','系统用户表','lbs-server');
insert into ops.t_application_properties(profile,key, value, comment, application) values('dev','spring.table.t_log','t_log','API调用日志表','lbs-server');
insert into ops.t_application_properties(profile,key, value, comment, application) values('dev','spring.table.t_tilemapservice','t_tilemapservice','瓦片地图服务表','lbs-server');
insert into ops.t_application_properties(profile,key, value, comment, application) values('dev','spring.table.t_tilemap','t_tilemap','瓦片地图表','lbs-server');
insert into ops.t_application_properties(profile,key, value, comment, application) values('dev','spring.table.t_tileset','t_tileset','瓦片数据集表','lbs-server');
insert into ops.t_application_properties(profile,key, value, comment, application) values('dev','spring.table.t_project','t_project','项目表','lbs-server');
insert into ops.t_application_properties(profile,key, value, comment, application) values('dev','spring.table.t_dataset','t_dataset','数据集表','lbs-server');
insert into ops.t_application_properties(profile,key, value, comment, application) values('dev','service.mapserver','localhost:8080','mapserver服务地址','lbs-server');
insert into ops.t_application_properties(profile,key, value, comment, application) values('dev','service.tiledata','F:/data/tile','瓦片数据路径','lbs-server');
insert into ops.t_application_properties(profile,key, value, comment, application) values('dev','dem.gujiao','e:/data/gujiao.tif','古交DEM','lbs-server');
insert into ops.t_application_properties(profile,key, value, comment, application) values('dev','dem.jingzhuang','e:/data/jzhdem_7m.tif','静庄DEM','lbs-server');


insert into ops.t_application_properties(profile,key, value, comment, application) values('prod','spring.table.t_sys_role','t_sys_role','系统角色表','lbs-server');
insert into ops.t_application_properties(profile,key, value, comment, application) values('prod','spring.table.t_sys_user','t_sys_user','系统用户表','lbs-server');
insert into ops.t_application_properties(profile,key, value, comment, application) values('prod','spring.table.t_log','t_log','API调用日志表','lbs-server');
insert into ops.t_application_properties(profile,key, value, comment, application) values('prod','spring.table.t_tilemapservice','t_tilemapservice','瓦片地图服务表','lbs-server');
insert into ops.t_application_properties(profile,key, value, comment, application) values('prod','spring.table.t_tilemap','t_tilemap','瓦片地图表','lbs-server');
insert into ops.t_application_properties(profile,key, value, comment, application) values('prod','spring.table.t_tileset','t_tileset','瓦片数据集表','lbs-server');
insert into ops.t_application_properties(profile,key, value, comment, application) values('prod','spring.table.t_project','t_project','项目表','lbs-server');
insert into ops.t_application_properties(profile,key, value, comment, application) values('prod','spring.table.t_dataset','t_dataset','数据集表','lbs-server');
insert into ops.t_application_properties(profile,key, value, comment, application) values('prod','service.mapserver','111.202.109.211:8080','mapserver服务地址','lbs-server');
insert into ops.t_application_properties(profile,key, value, comment, application) values('prod','service.tiledata','/home/dev/data/tile','瓦片数据路径','lbs-server');
insert into ops.t_application_properties(profile,key, value, comment, application) values('prod','dem.gujiao','/home/dev/data/gujiao.tif','古交DEM','lbs-server');
insert into ops.t_application_properties(profile,key, value, comment, application) values('prod','dem.jingzhuang','/home/dev/data/jzhdem_7m.tif','静庄DEM','lbs-server');

insert into ops.t_application_properties(profile,key, value, comment, application) values('prod','spring.img.path','/home/dev/data/img','上传图片文件夹','lbs-server');
insert into ops.t_application_properties(profile,key, value, comment, application) values('dev','spring.img.path','F:/data/img','上传图片文件夹','lbs-server');








