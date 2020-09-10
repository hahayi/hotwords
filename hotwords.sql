/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 80016
 Source Host           : localhost
 Source Database       : hotwords

 Target Server Type    : MySQL
 Target Server Version : 80016
 File Encoding         : utf-8

 Date: 09/10/2020 03:05:32 AM
*/

SET NAMES utf8;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
--  Table structure for `sys_hotwords`
-- ----------------------------
DROP TABLE IF EXISTS `sys_hotwords`;
CREATE TABLE `sys_hotwords` (
  `id` varchar(32) NOT NULL,
  `word` varchar(255) DEFAULT NULL,
  `seat` int(11) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `firsttime` datetime DEFAULT NULL,
  `lasttime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
--  Table structure for `sys_param`
-- ----------------------------
DROP TABLE IF EXISTS `sys_param`;
CREATE TABLE `sys_param` (
  `id` varchar(32) NOT NULL,
  `pname` varchar(50) DEFAULT NULL,
  `pvalue` varchar(255) DEFAULT NULL,
  `pdetail` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
--  Records of `sys_param`
-- ----------------------------
BEGIN;
INSERT INTO `sys_param` VALUES ('a31800e0f10d11eabb776c4f4a84b70b', 'hot_words_number', '15', '热词个数'), ('b93a08d6f10e11eabb776c4f4a84b70b', 'cron', '5', '刷新频率（小时）');
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
