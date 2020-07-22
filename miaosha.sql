/*
 Navicat Premium Data Transfer

 Source Server         : MySQL
 Source Server Type    : MySQL
 Source Server Version : 50729
 Source Host           : localhost:3306
 Source Schema         : miaosha

 Target Server Type    : MySQL
 Target Server Version : 50729
 File Encoding         : 65001

 Date: 22/07/2020 17:47:38
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for gms_goods
-- ----------------------------
DROP TABLE IF EXISTS `gms_goods`;
CREATE TABLE `gms_goods`  (
  `id` bigint(20) NOT NULL COMMENT '商品编号',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '商品名称',
  `subtitle` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '副标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '商品详情描述',
  `images` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商品图片',
  `price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '商品单价',
  `stock` int(11) NULL DEFAULT 0 COMMENT '库存数量',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_goods_name`(`name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gms_goods
-- ----------------------------
INSERT INTO `gms_goods` VALUES (1278941871975956480, 'Apple iPhone 11 (A2223) 128GB 黑色', 'Apple iPhone 11 (A2223) 128GB 黑色 移动联通电信4G手机 双卡双待', '品牌： Apple\r\n商品名称：AppleiPhone 11商品编号：100008348542商品毛重：470.00g商品产地：中国大陆CPU型号：其他运行内存：其他机身存储：128GB存储卡：不支持存储卡摄像头数量：后置双摄后摄主摄像素：1200万像素前摄主摄像素：1200万像素主屏幕尺寸（英寸）：6.1英寸分辨率：其它分辨率屏幕比例：其它屏幕比例屏幕前摄组合：刘海屏充电器：其他热点：人脸识别特殊功能：语音命令操作系统：iOS(Apple)', '', 5999.00, 1000, '2020-07-03 06:44:53', NULL);

-- ----------------------------
-- Table structure for oms_order
-- ----------------------------
DROP TABLE IF EXISTS `oms_order`;
CREATE TABLE `oms_order`  (
  `id` bigint(20) NOT NULL COMMENT '订单编号',
  `member_id` bigint(20) NOT NULL COMMENT '会员编号',
  `receiver_address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收货地址',
  `receiver_phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '收获人电话',
  `status` int(1) NOT NULL DEFAULT 0 COMMENT '订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单',
  `total_amount` decimal(10, 2) NULL DEFAULT NULL COMMENT '订单总金额',
  `order_type` int(1) NOT NULL DEFAULT 1 COMMENT '订单类型：1->普通订单；2->秒杀订单',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '订单生成时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for oms_order_item
-- ----------------------------
DROP TABLE IF EXISTS `oms_order_item`;
CREATE TABLE `oms_order_item`  (
  `id` bigint(20) NOT NULL COMMENT '行项目编号',
  `order_id` bigint(20) NOT NULL COMMENT '订单编号',
  `goods_id` bigint(20) NOT NULL COMMENT '商品编号',
  `goods_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '商品名称',
  `goods_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '销售价格',
  `quantity` int(11) NULL DEFAULT NULL COMMENT '购买数量',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '订单行项目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sec_goods
-- ----------------------------
DROP TABLE IF EXISTS `sec_goods`;
CREATE TABLE `sec_goods`  (
  `seckill_id` bigint(20) NOT NULL COMMENT '秒杀商品编号',
  `goods_id` bigint(20) NOT NULL COMMENT '商品编号',
  `seckill_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '秒杀价格',
  `seckill_stock` int(11) NULL DEFAULT 0 COMMENT '秒杀库存',
  `start_time` datetime(0) NULL DEFAULT NULL COMMENT '秒杀开始时间',
  `end_time` datetime(0) NULL DEFAULT NULL COMMENT '秒杀结束时间',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`seckill_id`) USING BTREE,
  INDEX `idx_sec_goods_id`(`goods_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '秒杀商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of sec_goods
-- ----------------------------
INSERT INTO `sec_goods` VALUES (1278943001673990144, 1278941871975956480, 5499.00, 99, '2020-07-10 10:00:00', '2020-07-10 12:00:00', '2020-07-03 06:47:38', NULL);

-- ----------------------------
-- Table structure for ums_member
-- ----------------------------
DROP TABLE IF EXISTS `ums_member`;
CREATE TABLE `ums_member`  (
  `id` bigint(20) UNSIGNED NOT NULL COMMENT '主键id',
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '登录密码',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '手机号码',
  `email` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '邮箱地址',
  `status` int(2) NOT NULL DEFAULT 1 COMMENT '状态，启用-1，禁用-0',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_username`(`username`) USING BTREE,
  UNIQUE INDEX `idx_email`(`email`) USING BTREE,
  UNIQUE INDEX `idx_phone`(`phone`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '会员表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ums_member
-- ----------------------------
INSERT INTO `ums_member` VALUES (1277933522262163456, 'test01', '$2a$10$HP5apPvJmA85q6AsRrJkwemXHylr2QuWqRJMtyE1GKi5gkO.oyg..', NULL, '10011@163.com', 1, '2020-06-30 19:54:29', NULL);
INSERT INTO `ums_member` VALUES (1277934092729450496, 'test02', '$2a$10$uQ88A4jNcAwYGKRIoWC3I.sxgbNT12Fbx5kckbzjx4hZvouGFp8/C', NULL, '20011@163.com', 1, '2020-06-30 19:56:45', NULL);

-- ----------------------------
-- Table structure for ums_member_role
-- ----------------------------
DROP TABLE IF EXISTS `ums_member_role`;
CREATE TABLE `ums_member_role`  (
  `user_id` bigint(20) UNSIGNED NOT NULL COMMENT '用户id',
  `role_id` bigint(20) UNSIGNED NOT NULL COMMENT '角色id',
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '会员-角色关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ums_member_role
-- ----------------------------
INSERT INTO `ums_member_role` VALUES (1277933522262163456, 1277841668322897941);
INSERT INTO `ums_member_role` VALUES (1277934092729450496, 1277841668322897941);
INSERT INTO `ums_member_role` VALUES (1277934092729450496, 1277841668322897942);

-- ----------------------------
-- Table structure for ums_role
-- ----------------------------
DROP TABLE IF EXISTS `ums_role`;
CREATE TABLE `ums_role`  (
  `id` bigint(20) UNSIGNED NOT NULL COMMENT '主键id',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '描述',
  `create_time` datetime(0) NOT NULL COMMENT '创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of ums_role
-- ----------------------------
INSERT INTO `ums_role` VALUES (1277841668322897941, '普通会员', 'level-1', '2020-06-30 19:07:39', NULL);
INSERT INTO `ums_role` VALUES (1277841668322897942, '白银会员', 'level-2', '2020-06-30 19:08:14', NULL);

SET FOREIGN_KEY_CHECKS = 1;
