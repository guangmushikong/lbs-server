
/*
 * 一、安装扩展
 */
create extension postgis;
create extension pg_trgm;

/*
 * 二、Schema
 */
create schema data AUTHORIZATION postgres;
comment on schema data is '数据存储';
create schema udf AUTHORIZATION postgres;
comment on schema udf is '用户自定义函数';
create schema ops AUTHORIZATION postgres;
comment on schema data is '服务运维';

/*
 * 三、ops模式建表
 */

--1、服务应用表
create table ops.t_application
(
  id bigserial NOT NULL PRIMARY KEY,
  application text,
  comment text,
  create_time timestamp with time zone NOT NULL DEFAULT now(),
  modify_time timestamp with time zone NOT NULL DEFAULT now(),
  CONSTRAINT t_application_unique_index UNIQUE (application)
);
-- 创建注释
COMMENT ON TABLE ops.t_application IS '应用配置';
COMMENT ON COLUMN ops.t_application.application IS '应用名称';
COMMENT ON COLUMN ops.t_application.comment IS '注释';
COMMENT ON COLUMN ops.t_application.create_time IS '创建时间';
COMMENT ON COLUMN ops.t_application.modify_time IS '修改时间';

--2、服务应用配置表
create table ops.t_application_properties
(
  id bigserial NOT NULL PRIMARY KEY,
  key text,
  value text,
  comment text,
  application text NOT NULL,
  profile text NOT NULL DEFAULT 'dev',
  label text NOT NULL DEFAULT 'master',
  create_time timestamp with time zone NOT NULL DEFAULT now(),
  modify_time timestamp with time zone NOT NULL DEFAULT now(),
  CONSTRAINT t_application_properties_unique_index UNIQUE (application,profile,label,key)
);
-- 创建注释
COMMENT ON TABLE ops.t_application_properties IS '服务应用配置表';
COMMENT ON COLUMN ops.t_application_properties.key IS '配置名';
COMMENT ON COLUMN ops.t_application_properties.value IS '配置值';
COMMENT ON COLUMN ops.t_application_properties.comment IS '注释';
COMMENT ON COLUMN ops.t_application_properties.application IS '应用名称';
COMMENT ON COLUMN ops.t_application_properties.profile IS '环境名称。默认为dev';
COMMENT ON COLUMN ops.t_application_properties.label IS '分支名称。默认为master';
COMMENT ON COLUMN ops.t_application_properties.create_time IS '创建时间';
COMMENT ON COLUMN ops.t_application_properties.modify_time IS '修改时间';

/*
 * 五、public模式建表
 */
--1、系统角色表
create table t_sys_role
(
  id bigserial NOT NULL PRIMARY KEY,
  name text,
  comment text
);
-- 创建注释
COMMENT ON TABLE t_sys_role IS '系统角色表';
COMMENT ON COLUMN t_sys_role.name IS '名称';
COMMENT ON COLUMN t_sys_role.comment IS '注释';

--2、系统用户表
create table t_sys_user
(
  id bigserial NOT NULL PRIMARY KEY,
  username text,
  password text,
  nick text,
  mobile text,
  email text,
  role_id bigint NOT NULL,
  project_ids text,
  create_time timestamp with time zone NOT NULL DEFAULT now(),
  modify_time timestamp with time zone NOT NULL DEFAULT now()
);
-- 创建注释
COMMENT ON TABLE t_sys_user IS '系统用户表';
COMMENT ON COLUMN t_sys_user.nick IS '昵称';
COMMENT ON COLUMN t_sys_user.mobile IS '手机';
COMMENT ON COLUMN t_sys_user.username IS '用户';
COMMENT ON COLUMN t_sys_user.password IS '密码';
COMMENT ON COLUMN t_sys_user.email IS '邮箱';
COMMENT ON COLUMN t_sys_user.role_id IS '角色ID';
COMMENT ON COLUMN t_sys_user.create_time IS '创建时间';

--3、API调用日志表
create table t_log
(
  id bigserial NOT NULL,
  ip text,
  message text,
  method text,
  usetime bigint,
  log_time timestamp with time zone NOT NULL DEFAULT now()
);
-- 创建注释
COMMENT ON TABLE t_log IS 'API调用日志表';
COMMENT ON COLUMN t_log.ip IS '访问ip';
COMMENT ON COLUMN t_log.message IS '请求数据';
COMMENT ON COLUMN t_log.method IS '请求方法';
COMMENT ON COLUMN t_log.usetime IS '持续时间。单位ms';
COMMENT ON COLUMN t_log.log_time IS '创建时间';

--4、瓦片地图服务表
create table t_tilemapservice
(
  id bigserial NOT NULL,
  title text,
  abstract text,
  version text,
  kind smallint,
  href text
);
-- 创建注释
COMMENT ON TABLE t_tilemapservice IS '瓦片地图服务表';
COMMENT ON COLUMN t_tilemapservice.title IS '标题';
COMMENT ON COLUMN t_tilemapservice.abstract IS '摘要';
COMMENT ON COLUMN t_tilemapservice.version IS '版本';
COMMENT ON COLUMN t_tilemapservice.kind IS '服务类型。1为XYZ，2为TMS，3为WMTS';

--5、瓦片地图表
create table t_tilemap
(
    id bigserial NOT NULL PRIMARY KEY,
    name text NOT NULL,
    memo text,
	service_id bigint NOT NULL,
    kind smallint NOT NULL,
	layer_group text NOT NULL,
	tile_type text NOT NULL,
	epsg bigint NOT NULL,
    minx numeric,
    miny numeric,
    maxx numeric,
    maxy numeric,
    origin_x numeric,
    origin_y numeric,
    width integer,
    height integer,
    suffix text,
    record_date text,
    prop text,
    create_time timestamp with time zone NOT NULL DEFAULT now(),
    modify_time timestamp with time zone NOT NULL DEFAULT now(),
	CONSTRAINT t_tilemap_unique_index UNIQUE (name,service_id)
);

-- 创建注释
COMMENT ON TABLE t_tilemap IS '瓦片地图表';
COMMENT ON COLUMN t_tilemap.name IS '名称';
COMMENT ON COLUMN t_tilemap.memo IS '备注';
COMMENT ON COLUMN t_tilemap.service_id IS '服务ID';
COMMENT ON COLUMN t_tilemap.kind IS '类别。4TMS瓦片缓存、5TMS时序瓦片缓存、6PG图层、7XYZ瓦片缓存';
COMMENT ON COLUMN t_tilemap.layer_group IS '图层分组代码';
COMMENT ON COLUMN t_tilemap.tile_type IS '瓦片类型';
COMMENT ON COLUMN t_tilemap.epsg IS 'EPSG';
COMMENT ON COLUMN t_tilemap.minx IS '最小经度';
COMMENT ON COLUMN t_tilemap.miny IS '最小纬度';
COMMENT ON COLUMN t_tilemap.maxx IS '最大经度';
COMMENT ON COLUMN t_tilemap.maxy IS '最大纬度';
COMMENT ON COLUMN t_tilemap.origin_x IS '原点经度';
COMMENT ON COLUMN t_tilemap.origin_y IS '原点纬度';
COMMENT ON COLUMN t_tilemap.width IS '瓦片宽度';
COMMENT ON COLUMN t_tilemap.height IS '瓦片高度';
COMMENT ON COLUMN t_tilemap.suffix IS '瓦片文件扩展名';
COMMENT ON COLUMN t_tilemap.record_date IS '记录时间';
COMMENT ON COLUMN t_tilemap.prop IS '属性';
--6、瓦片数据集表
create table t_tileset
(
    id bigserial NOT NULL PRIMARY KEY,
    map_id bigint NOT NULL,
    href text,
    units_per_pixel text,
    sort_order bigint
);
-- 创建注释
COMMENT ON TABLE t_tileset IS '瓦片数据集表';
COMMENT ON COLUMN t_tileset.map_id IS '地图ID';
COMMENT ON COLUMN t_tileset.units_per_pixel IS '地面分辨率。一像素代表地面的距离（米），单位为像素/米';
COMMENT ON COLUMN t_tileset.sort_order IS '排列顺序';

--7、项目表
create table t_project
(
    id bigserial NOT NULL PRIMARY KEY,
    name text,
    memo text,
    dataset_ids text,
    create_time timestamp with time zone NOT NULL DEFAULT now(),
    modify_time timestamp with time zone NOT NULL DEFAULT now()
);
-- 创建注释
COMMENT ON TABLE t_project IS '项目表';
COMMENT ON COLUMN t_project.name IS '项目名称';
COMMENT ON COLUMN t_project.memo IS '备注';
COMMENT ON COLUMN t_project.create_time IS '创建时间';
COMMENT ON COLUMN t_project.modify_time IS '修改时间';

--8、数据集表
create table t_dataset
(
    id bigserial NOT NULL PRIMARY KEY,
    name text,
    memo text,
    record_date text,
    layer_group text,
    type smallint,
    create_time timestamp with time zone NOT NULL DEFAULT now(),
    modify_time timestamp with time zone NOT NULL DEFAULT now(),
    kind smallint,
    map_id bigint
);
-- 创建注释
COMMENT ON TABLE t_dataset IS '数据集表';
COMMENT ON COLUMN t_dataset.name IS '数据名称';
COMMENT ON COLUMN t_dataset.memo IS '备注';
COMMENT ON COLUMN t_dataset.record_date IS '数据记录日期，用于影像时间序列';
COMMENT ON COLUMN t_dataset.layer_group IS '图层分组。L0-L4';
COMMENT ON COLUMN t_dataset.type IS '数据类型。栅格、矢量、照片';
COMMENT ON COLUMN t_dataset.create_time IS '创建时间';
COMMENT ON COLUMN t_dataset.modify_time IS '修改时间';

--9、自定义数据集表
create table t_custom_data
(
    id bigserial NOT NULL PRIMARY KEY,
    name text NOT NULL,
	project_id bigint NOT NULL,
	user_id bigint NOT NULL,
	prop text,
    geom geometry,
    create_time timestamp with time zone NOT NULL DEFAULT now(),
    modify_time timestamp with time zone NOT NULL DEFAULT now()
);

--10、kml数据集表
create table t_kml_data
(
    id bigserial NOT NULL PRIMARY KEY,
    name text,
	user_name text,
	type text,
    geom geometry,
    create_time timestamp with time zone NOT NULL DEFAULT now()
);
