/*
SQLyog Ultimate v12.09 (64 bit)
MySQL - 5.5.40 : Database - battle
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`battle` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `battle`;

/*Table structure for table `t_battle` */

DROP TABLE IF EXISTS `t_battle`;

CREATE TABLE `t_battle` (
  `id` varchar(32) NOT NULL COMMENT 'id与对局设置id相同',
  `name` varchar(50) DEFAULT NULL COMMENT '对局名称',
  `map_id` varchar(32) DEFAULT NULL COMMENT '地图名称',
  `begin_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `status` int(1) DEFAULT NULL COMMENT '1进行中 2结束',
  `remark` varchar(100) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  KEY `map_id` (`map_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `t_battle_new_setting` */

DROP TABLE IF EXISTS `t_battle_new_setting`;

CREATE TABLE `t_battle_new_setting` (
  `id` varchar(32) NOT NULL,
  `name` varchar(32) DEFAULT NULL,
  `map_id` varchar(32) DEFAULT NULL,
  `red_team_list` longtext,
  `blue_team_list` longtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `t_battle_setting` */

DROP TABLE IF EXISTS `t_battle_setting`;

CREATE TABLE `t_battle_setting` (
  `id` varchar(32) NOT NULL COMMENT 'id',
  `name` varchar(50) DEFAULT NULL COMMENT '对局设置名称',
  `map_id` varchar(32) DEFAULT NULL COMMENT '引用地图id',
  `red_vest_num_str` varchar(255) DEFAULT NULL COMMENT '红队人员编号',
  `blue_vest_num_str` varchar(255) DEFAULT NULL COMMENT '蓝队人员编号',
  `red_vest_weapon` varchar(255) DEFAULT NULL COMMENT '红队武器类型',
  `blue_vest_weapon` varchar(255) DEFAULT NULL COMMENT '蓝队武器类型',
  `mode` int(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `map_id` (`map_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `t_com` */

DROP TABLE IF EXISTS `t_com`;

CREATE TABLE `t_com` (
  `id` varchar(32) NOT NULL,
  `port_name` varchar(5) DEFAULT NULL,
  `baud_rate` int(11) DEFAULT NULL,
  `status` int(1) DEFAULT NULL COMMENT '0正常\r\n1异常',
  `msg` varchar(100) DEFAULT NULL COMMENT '信息',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `t_map` */

DROP TABLE IF EXISTS `t_map`;

CREATE TABLE `t_map` (
  `id` varchar(32) NOT NULL COMMENT 'id',
  `name` varchar(20) DEFAULT NULL COMMENT '地图名称',
  `path` varchar(100) DEFAULT NULL COMMENT '原图路径',
  `left_top_lng` double(15,10) DEFAULT NULL COMMENT '左上角经度',
  `left_top_lat` double(15,10) DEFAULT NULL COMMENT '左上角纬度',
  `right_down_lng` double(15,10) DEFAULT NULL COMMENT '右下经度',
  `right_down_lat` double(15,10) DEFAULT NULL COMMENT '右下维度',
  `time` datetime DEFAULT NULL COMMENT '创建时间',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `t_new_battle` */

DROP TABLE IF EXISTS `t_new_battle`;

CREATE TABLE `t_new_battle` (
  `id` varchar(32) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  `map_id` varchar(32) DEFAULT NULL,
  `begin_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `status` int(1) DEFAULT NULL COMMENT '1进行中 2结束',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `t_new_record` */

DROP TABLE IF EXISTS `t_new_record`;

CREATE TABLE `t_new_record` (
  `id` varchar(32) NOT NULL,
  `battle_id` varchar(32) DEFAULT NULL,
  `time` datetime DEFAULT NULL,
  `path` varchar(100) DEFAULT NULL,
  `record_data` longtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `t_record` */

DROP TABLE IF EXISTS `t_record`;

CREATE TABLE `t_record` (
  `id` varchar(32) NOT NULL,
  `path` varchar(100) DEFAULT NULL COMMENT '视频路径',
  `time` datetime DEFAULT NULL,
  `name` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
