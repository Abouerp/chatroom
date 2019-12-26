/*
 Navicat Premium Data Transfer

 Source Server         : faceservice
 Source Server Type    : MySQL
 Source Server Version : 50728
 Source Host           : 106.12.84.152:3307
 Source Schema         : socket

 Target Server Type    : MySQL
 Target Server Version : 50728
 File Encoding         : 65001

 Date: 26/12/2019 22:46:13
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for test
-- ----------------------------
DROP TABLE IF EXISTS `test`;
CREATE TABLE `test`  (
  `name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL,
  `keyword` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of test
-- ----------------------------
INSERT INTO `test` VALUES ('root', '123456');
INSERT INTO `test` VALUES ('admin', 'admin');
INSERT INTO `test` VALUES ('kkk', 'kkk');
INSERT INTO `test` VALUES ('111', '111');
INSERT INTO `test` VALUES ('123', '123');
INSERT INTO `test` VALUES ('132', '132');
INSERT INTO `test` VALUES ('bbb', 'bbb');
INSERT INTO `test` VALUES ('test', 'test');

SET FOREIGN_KEY_CHECKS = 1;
