/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 50729
Source Host           : localhost:3306
Source Database       : dataapi

Target Server Type    : MYSQL
Target Server Version : 50729
File Encoding         : 65001

Date: 2022-02-21 18:56:11
*/

SET
FOREIGN_KEY_CHECKS=0;
-- create
-- database if not exists dataapi;
-- ----------------------------
-- Table structure for icredit_api_base
-- ----------------------------
DROP TABLE IF EXISTS icredit_api_base;
CREATE TABLE icredit_api_base
(
    `id`             varchar(30) NOT NULL,
    `type`           tinyint(1) DEFAULT NULL,
    `name`           varchar(100) DEFAULT NULL,
    `path`           varchar(500) DEFAULT NULL,
    `request_type`   varchar(10)  DEFAULT NULL,
    `response_type`  varchar(10)  DEFAULT NULL,
    `desc`           varchar(500) DEFAULT NULL,
    `api_group_id`   varchar(30)  DEFAULT NULL,
    `api_version`    int(11) DEFAULT NULL,
    `publish_status` tinyint(1) DEFAULT NULL,
    `publish_user`   varchar(30)  DEFAULT NULL,
    `publish_time`   datetime     DEFAULT NULL,
    `remark`         varchar(255) DEFAULT NULL,
    `create_time`    datetime     DEFAULT NULL,
    `create_by`      varchar(30)  DEFAULT NULL,
    `update_time`    datetime     DEFAULT NULL,
    `update_by`      varchar(30)  DEFAULT NULL,
    `del_flag`       tinyint(1) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='API表基础信息表';

-- ----------------------------
-- Records of icredit_api_base
-- ----------------------------

-- ----------------------------
-- Table structure for icredit_api_base_hi
-- ----------------------------
DROP TABLE IF EXISTS icredit_api_base_hi;
CREATE TABLE icredit_api_base_hi
(
    `id`             varchar(30) NOT NULL,
    `type`           tinyint(1) DEFAULT NULL,
    `name`           varchar(100) DEFAULT NULL,
    `path`           varchar(500) DEFAULT NULL,
    `request_type`   varchar(10)  DEFAULT NULL,
    `response_type`  varchar(10)  DEFAULT NULL,
    `desc`           varchar(500) DEFAULT NULL,
    `api_group_id`   varchar(30)  DEFAULT NULL,
    `api_version`    int(11) DEFAULT NULL,
    `publish_status` tinyint(1) DEFAULT NULL,
    `publish_user`   varchar(30)  DEFAULT NULL,
    `publish_time`   datetime     DEFAULT NULL,
    `api_base_id`    varchar(30)  DEFAULT NULL,
    `remark`         varchar(255) DEFAULT NULL,
    `create_time`    datetime     DEFAULT NULL,
    `create_by`      varchar(30)  DEFAULT NULL,
    `update_time`    datetime     DEFAULT NULL,
    `update_by`      varchar(30)  DEFAULT NULL,
    `del_flag`       tinyint(1) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='API表基础信息历史版本表';

-- ----------------------------
-- Records of icredit_api_base_hi
-- ----------------------------

-- ----------------------------
-- Table structure for icredit_api_group
-- ----------------------------
DROP TABLE IF EXISTS icredit_api_group;
CREATE TABLE icredit_api_group
(
    `id`          varchar(30) NOT NULL,
    `work_id`     varchar(30)  DEFAULT NULL,
    `name`        varchar(100) DEFAULT NULL,
    `desc`        varchar(200) DEFAULT NULL,
    `remark`      varchar(255) DEFAULT NULL,
    `create_time` datetime     DEFAULT NULL,
    `create_by`   varchar(30)  DEFAULT NULL,
    `update_time` datetime     DEFAULT NULL,
    `update_by`   varchar(30)  DEFAULT NULL,
    `del_flag`    tinyint(1) DEFAULT NULL,
    `sort`        int(11) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='API分组';


-- ----------------------------
-- Table structure for icredit_api_log
-- ----------------------------
DROP TABLE IF EXISTS icredit_api_log;
CREATE TABLE icredit_api_log (
  `id` varchar(30) NOT NULL,
  `api_name` varchar(50) DEFAULT NULL,
  `api_id` varchar(30) DEFAULT NULL,
  `api_path` varchar(100) DEFAULT NULL,
  `app_name` varchar(100) DEFAULT NULL,
  `app_id` varchar(30) DEFAULT NULL,
  `call_ip` varchar(100) DEFAULT NULL,
  `api_version` int(11) DEFAULT NULL,
  `request_protocol` varchar(20) DEFAULT NULL,
  `request_type` varchar(20) DEFAULT NULL,
  `response_type` varchar(20) DEFAULT NULL,
  `request_param` text,
  `response_param` text,
  `call_begin_time` datetime DEFAULT NULL,
  `call_end_time` datetime DEFAULT NULL,
  `run_time` bigint(16) DEFAULT NULL,
  `call_status` tinyint(1) DEFAULT NULL,
  `remark` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `create_by` varchar(30) DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `update_by` varchar(30) DEFAULT NULL,
  `del_flag` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='调用日志';

-- ----------------------------
-- Records of icredit_api_log
-- ----------------------------
drop table if exists icredit_api_log_summary;

/*==============================================================*/
/* Table: icredit_api_log_summary                               */
/*==============================================================*/
create table icredit_api_log_summary
(
    id              varchar(30) not null,
    api_name        varchar(50),
    api_id          varchar(30),
    api_path        varchar(100),
    app_name        varchar(100),
    app_id          varchar(30),
    api_version     varchar(10),
    call_status     tinyint(1),
    call_status_str varchar(10),
    call_begin_time varchar(50),
    call_end_time   varchar(50),
    run_time        varchar(10),
    remark          varchar(255),
    create_time     datetime,
    create_by       varchar(30),
    update_time     datetime,
    update_by       varchar(30),
    del_flag        tinyint(1),
    primary key (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='日志摘要(列表)';

-- ----------------------------
-- Table structure for icredit_api_param
-- ----------------------------
DROP TABLE IF EXISTS icredit_api_param;
CREATE TABLE icredit_api_param
(
    `id`            varchar(30) NOT NULL,
    `field_name`    varchar(50)  DEFAULT NULL,
    `field_type`    varchar(20)  DEFAULT NULL,
    `required`      tinyint(1) DEFAULT NULL,
    `desc`          varchar(200) DEFAULT NULL,
    `is_request`    tinyint(1) DEFAULT NULL,
    `is_response`   tinyint(1) DEFAULT NULL,
    `api_base_id`   varchar(30)  DEFAULT NULL,
    `default_value` varchar(120)  DEFAULT NULL,
    `api_version`   tinyint(1) DEFAULT NULL,
    `remark`        varchar(255) DEFAULT NULL,
    `create_time`   datetime     DEFAULT NULL,
    `create_by`     varchar(30)  DEFAULT NULL,
    `update_time`   datetime     DEFAULT NULL,
    `update_by`     varchar(30)  DEFAULT NULL,
    `del_flag`      tinyint(1) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='api参数';

-- ----------------------------
-- Records of icredit_api_param
-- ----------------------------

-- ----------------------------
-- Table structure for icredit_app
-- ----------------------------
DROP TABLE IF EXISTS icredit_app;
CREATE TABLE icredit_app
(
    `id`                 varchar(30) NOT NULL,
    `app_flag`           varchar(30)  DEFAULT NULL,
    `certification_type` tinyint(1) DEFAULT NULL,
    `is_enable`          tinyint(1) DEFAULT NULL,
    `name`               varchar(50)  DEFAULT NULL,
    `secret_content`     text,
    `app_group_id`       varchar(30)  DEFAULT NULL,
    `desc`               varchar(255) DEFAULT NULL,
    `period`             int(11) DEFAULT NULL,
    `allow_ip`           varchar(500) DEFAULT NULL,
    `remark`             varchar(255) DEFAULT NULL,
    `create_time`        datetime     DEFAULT NULL,
    `create_by`          varchar(30)  DEFAULT NULL,
    `update_time`        datetime     DEFAULT NULL,
    `update_by`          varchar(30)  DEFAULT NULL,
    `del_flag`           tinyint(1) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用';

-- ----------------------------
-- Records of icredit_app
-- ----------------------------

-- ----------------------------
-- Table structure for icredit_app_group
-- ----------------------------
DROP TABLE IF EXISTS icredit_app_group;
CREATE TABLE icredit_app_group
(
    `id`          varchar(30) NOT NULL,
    `name`        varchar(50)  DEFAULT NULL,
    `desc`        varchar(255) DEFAULT NULL,
    `remark`      varchar(255) DEFAULT NULL,
    `create_time` datetime     DEFAULT NULL,
    `create_by`   varchar(30)  DEFAULT NULL,
    `update_time` datetime     DEFAULT NULL,
    `update_by`   varchar(30)  DEFAULT NULL,
    `del_flag`    tinyint(1) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='应用分组';

-- ----------------------------
-- Records of icredit_app_group
-- ----------------------------

-- ----------------------------
-- Table structure for icredit_auth
-- ----------------------------
DROP TABLE IF EXISTS icredit_auth;
CREATE TABLE `icredit_auth`
(
    `id`             varchar(30) NOT NULL,
    `app_id`         varchar(30)  DEFAULT NULL,
    `api_id`         varchar(30)  DEFAULT NULL,
    `auth_config_id` varchar(30)  DEFAULT NULL,
    `remark`         varchar(255) DEFAULT NULL,
    `create_time`    datetime     DEFAULT NULL,
    `create_by`      varchar(30)  DEFAULT NULL,
    `update_time`    datetime     DEFAULT NULL,
    `update_by`      varchar(30)  DEFAULT NULL,
    `del_flag`       tinyint(1) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='授权表';

-- ----------------------------
-- Records of icredit_auth
-- ----------------------------

-- ----------------------------
-- Table structure for icredit_auth_config
-- ----------------------------
DROP TABLE IF EXISTS icredit_auth_config;
CREATE TABLE icredit_auth_config
(
    `id`           varchar(30) NOT NULL,
    `period_begin` bigint(1) DEFAULT NULL,
    `period_end`   bigint(1) DEFAULT NULL,
    `allow_call`   int(11) DEFAULT NULL,
    `remark`       varchar(255) DEFAULT NULL,
    `create_time`  datetime     DEFAULT NULL,
    `create_by`    varchar(30)  DEFAULT NULL,
    `update_time`  datetime     DEFAULT NULL,
    `update_by`    varchar(30)  DEFAULT NULL,
    `del_flag`     tinyint(1) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='授权配置';

-- ----------------------------
-- Records of icredit_auth_config
-- ----------------------------

-- ----------------------------
-- Table structure for icredit_generate_api
-- ----------------------------
DROP TABLE IF EXISTS icredit_generate_api;
CREATE TABLE icredit_generate_api
(
    `id`            varchar(30) NOT NULL,
    `model`         tinyint(1) DEFAULT NULL,
    `datasource_id` varchar(30)  DEFAULT NULL,
    `table_name`    varchar(100) DEFAULT NULL,
    `sql`           text,
    `api_base_id`   varchar(30)  DEFAULT NULL,
    `api_version`   tinyint(1) DEFAULT NULL,
    `remark`        varchar(255) DEFAULT NULL,
    `create_time`   datetime     DEFAULT NULL,
    `create_by`     varchar(30)  DEFAULT NULL,
    `update_time`   datetime     DEFAULT NULL,
    `update_by`     varchar(30)  DEFAULT NULL,
    `del_flag`      tinyint(1) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='生成API表';

-- ----------------------------
-- Records of icredit_generate_api
-- ----------------------------

-- ----------------------------
-- Table structure for icredit_register_api
-- ----------------------------
DROP TABLE IF EXISTS icredit_register_api;
CREATE TABLE icredit_register_api
(
    `id`          varchar(30) NOT NULL,
    `host`        varchar(100) DEFAULT NULL,
    `path`        varchar(200) DEFAULT NULL,
    `api_base_id` varchar(30)  DEFAULT NULL,
    `api_version` tinyint(1) DEFAULT NULL,
    `remark`      varchar(255) DEFAULT NULL,
    `create_time` datetime     DEFAULT NULL,
    `create_by`   varchar(30)  DEFAULT NULL,
    `update_time` datetime     DEFAULT NULL,
    `update_by`   varchar(30)  DEFAULT NULL,
    `del_flag`    tinyint(1) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='注册API';

-- ----------------------------
-- Records of icredit_register_api
-- ----------------------------

-- ----------------------------
-- Table structure for icredit_work_flow
-- ----------------------------
DROP TABLE IF EXISTS icredit_work_flow;
CREATE TABLE icredit_work_flow
(
    `id`          varchar(30) NOT NULL,
    `name`        varchar(50)  DEFAULT NULL,
    `desc`        varchar(200) DEFAULT NULL,
    `remark`      varchar(255) DEFAULT NULL,
    `create_time` datetime     DEFAULT NULL,
    `create_by`   varchar(30)  DEFAULT NULL,
    `update_time` datetime     DEFAULT NULL,
    `update_by`   varchar(30)  DEFAULT NULL,
    `del_flag`    tinyint(1) DEFAULT NULL,
    `sort`        int(11) NOT NULL,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='业务流程';

-- ----------------------------
-- Records of icredit_work_flow
-- ----------------------------
INSERT INTO icredit_work_flow
VALUES ('0', '业务流程', '默认业务流程', null, '2022-02-21 17:56:17', 'admin', '2022-02-21 17:56:17', 'admin', '0', '2');

-- ----------------------------
-- Records of icredit_api_group
-- ----------------------------
INSERT INTO icredit_api_group
VALUES ('000', '0', '默认分组', '默认业务流程下的默认分组', null, '2022-02-21 17:58:06', 'admin', '2022-02-21 17:58:06', 'admin', '0',
        '2');

ALTER TABLE icredit_app ADD token_type int(11) COMMENT 'token类型：0-长期，1-8小时，2-自定义';
ALTER TABLE icredit_app ADD generateId varchar(30) COMMENT '应用id';

ALTER TABLE icredit_api_param ADD table_name varchar(100) COMMENT '表名称';

ALTER TABLE icredit_api_param ADD table_name varchar(100) COMMENT '表名称';

ALTER TABLE icredit_app_group ADD generate_id varchar(30) COMMENT '分组id';
ALTER TABLE icredit_app ADD generate_id varchar(30) COMMENT '应用id';

ALTER TABLE icredit_app DROP app_flag;

ALTER TABLE icredit_api_log ADD api_type tinyint(1) COMMENT 'api类型';
ALTER TABLE icredit_api_log ADD exception_detail text COMMENT '异常详情';
ALTER TABLE icredit_api_log ADD execute_sql text COMMENT '执行sql';
ALTER TABLE icredit_api_log ADD trace_id varchar(40) COMMENT 'kakafa幂等性ID';

ALTER TABLE icredit_api_base ADD interface_source tinyint(1) default 0 COMMENT 'API来源：0-内部，1-外部';
alter table icredit_app_group change `desc` `desc` varchar(255) COMMENT '分组描述';
alter table icredit_work_flow change `desc` `desc` varchar(255) COMMENT '业务流程描述';
alter table icredit_api_base change `desc` `desc` text COMMENT 'sql描述';
ALTER TABLE icredit_api_base ADD req_host varchar(30) COMMENT '后台服务Host';
ALTER TABLE icredit_api_base ADD req_path varchar(255) COMMENT '后台path';

ALTER TABLE icredit_api_base_hi ADD req_host varchar(30) COMMENT '后台服务Host';
ALTER TABLE icredit_api_base_hi ADD req_path varchar(255) COMMENT '后台path';