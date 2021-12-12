/*
 Navicat Premium Data Transfer

 Source Server         : TencentMySQL
 Source Server Type    : MySQL
 Source Server Version : 80023
 Source Host           : 42.192.89.44:3306
 Source Schema         : test_java7

 Target Server Type    : MySQL
 Target Server Version : 80023
 File Encoding         : 65001

 Date: 13/12/2021 00:58:21
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for order_detail
-- ----------------------------
DROP TABLE IF EXISTS `order_detail`;
CREATE TABLE `order_detail` (
  `order_detail_id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键ID,订单详情表ID',
  `order_id` int unsigned NOT NULL COMMENT '订单表ID',
  `product_id` int unsigned NOT NULL COMMENT '订单商品ID',
  `product_name` varchar(50) NOT NULL COMMENT '商品名称',
  `product_cnt` int NOT NULL DEFAULT '1' COMMENT '购买商品数量',
  `product_price` decimal(8,2) NOT NULL COMMENT '购买商品单价',
  `average_cost` decimal(8,2) NOT NULL DEFAULT '0.00' COMMENT '平均成本价格',
  `weight` float DEFAULT NULL COMMENT '商品重量',
  `fee_money` decimal(8,2) NOT NULL DEFAULT '0.00' COMMENT '优惠分摊金额',
  `actual_money` decimal(8,2) NOT NULL DEFAULT '0.00' COMMENT '实付金额',
  `modified_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`order_detail_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单详情表';

-- ----------------------------
-- Table structure for order_master
-- ----------------------------
DROP TABLE IF EXISTS `order_master`;
CREATE TABLE `order_master` (
  `order_id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_sn` bigint unsigned NOT NULL COMMENT '订单编号 yyyymmddnnnnnnnn',
  `customer_id` int unsigned NOT NULL COMMENT '下单人ID',
  `shipping_user` varchar(10) NOT NULL COMMENT '收货人姓名',
  `province` smallint NOT NULL COMMENT '收货人所在省',
  `city` smallint NOT NULL COMMENT '收货人所在市',
  `district` smallint NOT NULL COMMENT '收货人所在区',
  `address` varchar(100) NOT NULL COMMENT '收货人详细地址',
  `payment_method` tinyint NOT NULL COMMENT '支付方式:1余额,2网银,3微信,4支付宝,5到付',
  `order_money` decimal(8,2) NOT NULL COMMENT '订单金额',
  `district_money` decimal(8,2) NOT NULL DEFAULT '0.00' COMMENT '优惠金额',
  `shipping_money` decimal(8,2) NOT NULL DEFAULT '0.00' COMMENT '运费金额',
  `payment_money` decimal(8,2) NOT NULL DEFAULT '0.00' COMMENT '支付金额',
  `shipping_comp_name` varchar(10) DEFAULT NULL COMMENT '快递公司名称',
  `shipping_sn` varchar(50) DEFAULT NULL COMMENT '快递单号',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `shipping_time` datetime DEFAULT NULL COMMENT '发货时间',
  `pay_time` datetime DEFAULT NULL COMMENT '支付时间',
  `receive_time` datetime DEFAULT NULL COMMENT '收货时间',
  `order_status` tinyint NOT NULL DEFAULT '0' COMMENT '订单状态',
  `order_point` int unsigned NOT NULL DEFAULT '0' COMMENT '订单积分',
  `invoice_title` varchar(100) DEFAULT NULL COMMENT '发票抬头',
  `modified_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`order_id`),
  UNIQUE KEY `ux_ordersn` (`order_sn`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单主表';

-- ----------------------------
-- Table structure for product_brand_info
-- ----------------------------
DROP TABLE IF EXISTS `product_brand_info`;
CREATE TABLE `product_brand_info` (
  `brand_id` smallint unsigned NOT NULL AUTO_INCREMENT COMMENT '品牌ID',
  `brand_name` varchar(50) NOT NULL COMMENT '品牌名称',
  `telephone` varchar(50) NOT NULL COMMENT '官方总机联系电话',
  `brand_web` varchar(100) DEFAULT NULL COMMENT '品牌网站',
  `brand_logo` varchar(100) DEFAULT NULL COMMENT '品牌logo URL',
  `brand_desc` varchar(150) DEFAULT NULL COMMENT '品牌描述',
  `brand_status` tinyint NOT NULL DEFAULT '0' COMMENT '品牌状态,0禁用,1启用',
  `brand_order` tinyint NOT NULL DEFAULT '0' COMMENT '排序',
  `modified_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`brand_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='品牌信息表';

-- ----------------------------
-- Table structure for product_category
-- ----------------------------
DROP TABLE IF EXISTS `product_category`;
CREATE TABLE `product_category` (
  `category_id` smallint unsigned NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `category_name` varchar(10) NOT NULL COMMENT '分类名称',
  `category_code` varchar(10) NOT NULL COMMENT '分类编码',
  `parent_id` smallint unsigned NOT NULL DEFAULT '0' COMMENT '父分类ID',
  `category_level` tinyint NOT NULL DEFAULT '1' COMMENT '分类层级',
  `category_status` tinyint NOT NULL DEFAULT '1' COMMENT '分类状态',
  `modified_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品分类表';

-- ----------------------------
-- Table structure for product_comment
-- ----------------------------
DROP TABLE IF EXISTS `product_comment`;
CREATE TABLE `product_comment` (
  `comment_id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `product_id` int unsigned NOT NULL COMMENT '商品ID',
  `order_id` bigint unsigned NOT NULL COMMENT '订单ID',
  `customer_id` int unsigned NOT NULL COMMENT '用户ID',
  `title` varchar(50) NOT NULL COMMENT '评论标题',
  `content` varchar(300) NOT NULL COMMENT '评论内容',
  `audit_status` tinyint NOT NULL COMMENT '审核状态:0未审核1已审核',
  `audit_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '评论时间',
  `modified_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`comment_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品评论表';

-- ----------------------------
-- Table structure for product_info
-- ----------------------------
DROP TABLE IF EXISTS `product_info`;
CREATE TABLE `product_info` (
  `product_id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `product_code` char(16) NOT NULL COMMENT '商品编码',
  `product_name` varchar(50) NOT NULL COMMENT '商品名称',
  `bar_code` varchar(50) NOT NULL COMMENT '国条码',
  `brand_id` int unsigned NOT NULL COMMENT '品牌表的ID',
  `first_category_id` smallint unsigned NOT NULL COMMENT '一级分类ID',
  `second_category_id` smallint unsigned NOT NULL COMMENT '二级分类ID',
  `third_category_id` smallint unsigned NOT NULL COMMENT '三级分类ID',
  `supplier_id` int unsigned NOT NULL COMMENT '商品的供应商id',
  `price` decimal(8,2) NOT NULL COMMENT '商品销售价格',
  `average_cost` decimal(18,2) NOT NULL COMMENT '商品加权平均成本',
  `publish_status` tinyint NOT NULL DEFAULT '0' COMMENT '上下架状态:0下架1上架',
  `audit_status` tinyint NOT NULL DEFAULT '0' COMMENT '审核状态:0未审核,1已审核',
  `weight` float DEFAULT NULL COMMENT '商品重量',
  `length` float DEFAULT NULL COMMENT '商品长度',
  `heigh` float DEFAULT NULL COMMENT '商品高度',
  `width` float DEFAULT NULL COMMENT '商品宽度',
  `color_type` enum('红','黄','蓝','黒') DEFAULT NULL,
  `production_date` datetime NOT NULL COMMENT '生产日期',
  `shelf_life` int NOT NULL COMMENT '商品有效期',
  `descript` text NOT NULL COMMENT '商品描述',
  `indate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '商品录入时间',
  `modified_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品信息表';

-- ----------------------------
-- Table structure for product_pic_info
-- ----------------------------
DROP TABLE IF EXISTS `product_pic_info`;
CREATE TABLE `product_pic_info` (
  `product_pic_id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '商品图片ID',
  `product_id` int unsigned NOT NULL COMMENT '商品ID',
  `pic_desc` varchar(50) DEFAULT NULL COMMENT '图片描述',
  `pic_url` varchar(200) NOT NULL COMMENT '图片URL',
  `is_master` tinyint NOT NULL DEFAULT '0' COMMENT '是否主图:0.非主图1.主图',
  `pic_order` tinyint NOT NULL DEFAULT '0' COMMENT '图片排序',
  `pic_status` tinyint NOT NULL DEFAULT '1' COMMENT '图片是否有效:0无效 1有效',
  `modified_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`product_pic_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品图片信息表';

-- ----------------------------
-- Table structure for product_supplier_info
-- ----------------------------
DROP TABLE IF EXISTS `product_supplier_info`;
CREATE TABLE `product_supplier_info` (
  `supplier_id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '供应商ID',
  `supplier_code` char(8) NOT NULL COMMENT '供应商编码',
  `supplier_name` char(50) NOT NULL COMMENT '供应商名称',
  `supplier_type` tinyint NOT NULL COMMENT '供应商类型:1.自营,2.平台',
  `link_man` varchar(10) NOT NULL COMMENT '供应商联系人',
  `phone_number` varchar(50) NOT NULL COMMENT '联系电话',
  `bank_name` varchar(50) NOT NULL COMMENT '供应商开户银行名称',
  `bank_account` varchar(50) NOT NULL COMMENT '银行账号',
  `address` varchar(200) NOT NULL COMMENT '供应商地址',
  `supplier_status` tinyint NOT NULL DEFAULT '0' COMMENT '状态:0禁用,1启用',
  `modified_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`supplier_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品供应商信息表';

-- ----------------------------
-- Table structure for product_warehouse
-- ----------------------------
DROP TABLE IF EXISTS `product_warehouse`;
CREATE TABLE `product_warehouse` (
  `pw_id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '商品库存ID',
  `product_id` int unsigned NOT NULL COMMENT '商品id',
  `w_id` smallint unsigned NOT NULL COMMENT '仓库ID',
  `currnet_cnt` int unsigned NOT NULL DEFAULT '0' COMMENT '当前商品数量',
  `lock_cnt` int unsigned NOT NULL DEFAULT '0' COMMENT '当前占用数据',
  `in_transit_cnt` int unsigned NOT NULL DEFAULT '0' COMMENT '在途数据',
  `average_cost` decimal(8,2) NOT NULL DEFAULT '0.00' COMMENT '移动加权成本',
  `modified_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`pw_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品库存表';

-- ----------------------------
-- Table structure for user_addr
-- ----------------------------
DROP TABLE IF EXISTS `user_addr`;
CREATE TABLE `user_addr` (
  `user_addr_id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键ID',
  `user_id` int unsigned NOT NULL COMMENT 'user_login表的自增ID',
  `post_code` smallint NOT NULL COMMENT '邮编',
  `province` smallint NOT NULL COMMENT '地区表中省份的id',
  `city` smallint NOT NULL COMMENT '地区表中城市的id',
  `district` smallint NOT NULL COMMENT '地区表中的区id',
  `address` varchar(200) NOT NULL COMMENT '具体的地址门牌号',
  `is_default` tinyint NOT NULL COMMENT '是否默认',
  `modified_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`user_addr_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户地址表';

-- ----------------------------
-- Table structure for user_info
-- ----------------------------
DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
  `user_info_id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '自增主键ID',
  `user_id` int unsigned NOT NULL COMMENT 'user_login表的自增ID',
  `user_name` varchar(20) NOT NULL COMMENT '用户真实姓名',
  `identity_card_type` tinyint NOT NULL DEFAULT '1' COMMENT '证件类型：1 身份证,2军官证,3护照,4其他',
  `identity_card_no` varchar(20) DEFAULT NULL COMMENT '证件号码',
  `mobile_phone` int unsigned DEFAULT NULL COMMENT '手机号',
  `user_email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `gender` char(1) DEFAULT NULL COMMENT '性别',
  `user_point` int NOT NULL DEFAULT '0' COMMENT '用户积分',
  `register_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '注册时间',
  `birthday` datetime DEFAULT NULL COMMENT '会员生日',
  `user_level` tinyint NOT NULL DEFAULT '1' COMMENT '会员级别:1普通会员,2青铜会员,3白银会员,4黄金会员,5钻石会员',
  `user_money` decimal(8,2) NOT NULL DEFAULT '0.00' COMMENT '钱包余额',
  `modified_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`user_info_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户个人信息表';

-- ----------------------------
-- Table structure for user_login
-- ----------------------------
DROP TABLE IF EXISTS `user_login`;
CREATE TABLE `user_login` (
  `user_id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `login_name` varchar(20) NOT NULL COMMENT '用户登陆名',
  `password` char(32) NOT NULL COMMENT 'md5加密的密码',
  `user_stats` tinyint NOT NULL DEFAULT '1' COMMENT '用户状态',
  `modified_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户登陆表';

-- ----------------------------
-- Table structure for user_product_collection
-- ----------------------------
DROP TABLE IF EXISTS `user_product_collection`;
CREATE TABLE `user_product_collection` (
  `user_collection_id` int unsigned NOT NULL AUTO_INCREMENT COMMENT '收藏商品ID',
  `user_id` int unsigned NOT NULL COMMENT '用户ID',
  `product_id` int unsigned NOT NULL COMMENT '商品ID',
  `product_amount` int NOT NULL COMMENT '商品收藏数量',
  `price` decimal(8,2) NOT NULL COMMENT '商品价格',
  `add_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '商品收藏时间',
  `modified_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `product_status` tinyint NOT NULL COMMENT '商品状态:0下架 1有效',
  PRIMARY KEY (`user_collection_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='商品收藏表';

SET FOREIGN_KEY_CHECKS = 1;
