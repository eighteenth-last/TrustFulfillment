/*
 Navicat Premium Dump SQL

 Source Server         : WSL_MySQL
 Source Server Type    : MySQL
 Source Server Version : 80045 (8.0.45-0ubuntu0.24.04.1)
 Source Host           : 127.0.0.1:3306
 Source Schema         : trust_fulfillment

 Target Server Type    : MySQL
 Target Server Version : 80045 (8.0.45-0ubuntu0.24.04.1)
 File Encoding         : 65001

 Date: 06/02/2026 20:23:42
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_bank
-- ----------------------------
DROP TABLE IF EXISTS `sys_bank`;
CREATE TABLE `sys_bank`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `bank_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '银行代码',
  `bank_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '银行名称',
  `logo_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '银行Logo',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 0禁用, 1启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_bank_code`(`bank_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '银行信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_category
-- ----------------------------
DROP TABLE IF EXISTS `sys_category`;
CREATE TABLE `sys_category`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `parent_id` int NULL DEFAULT 0 COMMENT '父级ID，0表示一级分类',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '分类名称',
  `level` tinyint NOT NULL DEFAULT 1 COMMENT '分类级别：1一级，2二级，3三级',
  `icon` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '图标',
  `sort_order` int NULL DEFAULT 0 COMMENT '排序',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：0禁用，1启用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_parent`(`parent_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2014 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '业务分类表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `config_key` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置键',
  `config_value` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '配置值',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '描述',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_key`(`config_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 40 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统配置表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_login_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_login_log`;
CREATE TABLE `sys_login_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '用户ID',
  `login_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录方式: phone_code(手机验证码), phone_pwd(手机密码), wechat(微信)',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `openid` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微信openid',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '登录IP',
  `device` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '设备信息',
  `platform` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '平台: mp(小程序), h5, app',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 1成功, 0失败',
  `fail_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '失败原因',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 90 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户登录日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_notification
-- ----------------------------
DROP TABLE IF EXISTS `sys_notification`;
CREATE TABLE `sys_notification`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '接收用户ID',
  `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知类型: message, order, dispute, system',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '通知标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '通知内容',
  `target_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '关联目标类型',
  `target_id` bigint NULL DEFAULT NULL COMMENT '关联目标ID',
  `is_read` tinyint NULL DEFAULT 0 COMMENT '是否已读: 0否, 1是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `read_time` datetime NULL DEFAULT NULL COMMENT '已读时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_read`(`user_id` ASC, `is_read` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 27 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '系统通知表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_sms_code
-- ----------------------------
DROP TABLE IF EXISTS `sys_sms_code`;
CREATE TABLE `sys_sms_code`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '手机号',
  `code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '验证码',
  `type` tinyint NOT NULL COMMENT '类型: 1登录/注册, 2绑定手机, 3修改密码, 4支付验证',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求IP',
  `used` tinyint NULL DEFAULT 0 COMMENT '是否已使用: 0否, 1是',
  `expire_time` datetime NOT NULL COMMENT '过期时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_phone_type`(`phone` ASC, `type` ASC) USING BTREE,
  INDEX `idx_expire`(`expire_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '短信验证码表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_sms_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_sms_log`;
CREATE TABLE `sys_sms_log`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '手机号',
  `template_code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '短信模板编码',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '短信内容',
  `type` tinyint NOT NULL COMMENT '类型: 1验证码, 2通知, 3营销',
  `provider` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'aliyun' COMMENT '短信服务商: aliyun, tencent',
  `request_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '服务商请求ID',
  `biz_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '服务商业务ID',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态: 0发送中, 1成功, 2失败',
  `error_code` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '错误码',
  `error_msg` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '错误信息',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '请求IP',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_phone`(`phone` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '短信发送记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号 (主要登录标识)',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '加密密码 (可选,微信登录用户可能没有)',
  `nickname` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '头像URL',
  `gender` tinyint NULL DEFAULT 0 COMMENT '性别: 0未知, 1男, 2女',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'user' COMMENT '角色: user(用户), admin(管理员)',
  `is_merchant` tinyint NULL DEFAULT 0 COMMENT '是否有商家权限: 0否 1是',
  `merchant_id` bigint NULL DEFAULT NULL COMMENT '关联商户ID（有商家权限时）',
  `email` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '邮箱',
  `balance` decimal(12, 2) NULL DEFAULT 0.00 COMMENT '可用余额',
  `frozen_amount` decimal(12, 2) NULL DEFAULT 0.00 COMMENT '冻结/托管中金额',
  `credit_score` int NULL DEFAULT 600 COMMENT '信用分',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 1正常, 0禁用',
  `verified` tinyint NULL DEFAULT 0 COMMENT '是否实名认证: 1是, 0否',
  `real_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `id_card` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '身份证号(加密存储)',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '最后登录IP',
  `register_source` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'phone' COMMENT '注册来源: phone, wechat, admin',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_phone`(`phone` ASC) USING BTREE,
  INDEX `idx_role`(`role` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_merchant_id`(`merchant_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;

INSERT INTO `trust_fulfillment`.`sys_user` (`id`, `phone`, `password`, `nickname`, `avatar`, `gender`, `role`, `is_merchant`, `merchant_id`, `email`, `balance`, `frozen_amount`, `credit_score`, `status`, `verified`, `real_name`, `id_card`, `last_login_time`, `last_login_ip`, `register_source`, `create_time`, `update_time`) VALUES (1, '13272796154', '$2a$10$qhkb9LI41fewiRZneQ3UbOlHi8oFopuwOBLEngAUPIlLWU/kexj6W', '封灵天', NULL, 0, 'admin', 0, NULL, 'eighteenthstuai@gmail.com', 98967000.00, 711400.00, 800, 1, 0, NULL, NULL, '2026-02-06 19:18:15', '127.0.0.1', 'admin', '2026-01-30 16:10:40', '2026-02-06 19:18:15');
INSERT INTO `trust_fulfillment`.`sys_user` (`id`, `phone`, `password`, `nickname`, `avatar`, `gender`, `role`, `is_merchant`, `merchant_id`, `email`, `balance`, `frozen_amount`, `credit_score`, `status`, `verified`, `real_name`, `id_card`, `last_login_time`, `last_login_ip`, `register_source`, `create_time`, `update_time`) VALUES (2, '13800000001', '$2a$10$qhkb9LI41fewiRZneQ3UbOlHi8oFopuwOBLEngAUPIlLWU/kexj6W', '张小凡', NULL, 0, 'user', 1, 1, NULL, 67200.00, 0.00, 785, 1, 0, NULL, NULL, '2026-02-06 18:54:54', '127.0.0.1', 'phone', '2026-01-30 16:10:40', '2026-02-06 18:54:54');
INSERT INTO `trust_fulfillment`.`sys_user` (`id`, `phone`, `password`, `nickname`, `avatar`, `gender`, `role`, `is_merchant`, `merchant_id`, `email`, `balance`, `frozen_amount`, `credit_score`, `status`, `verified`, `real_name`, `id_card`, `last_login_time`, `last_login_ip`, `register_source`, `create_time`, `update_time`) VALUES (3, '13800000002', '$2a$10$qhkb9LI41fewiRZneQ3UbOlHi8oFopuwOBLEngAUPIlLWU/kexj6W', '测试', NULL, 0, 'user', 1, NULL, '', 442160.00, 0.00, 820, 1, 1, '测试', NULL, '2026-02-06 19:18:19', '127.0.0.1', 'phone', '2026-01-30 16:10:40', '2026-02-06 19:18:19');
INSERT INTO `trust_fulfillment`.`sys_user` (`id`, `phone`, `password`, `nickname`, `avatar`, `gender`, `role`, `is_merchant`, `merchant_id`, `email`, `balance`, `frozen_amount`, `credit_score`, `status`, `verified`, `real_name`, `id_card`, `last_login_time`, `last_login_ip`, `register_source`, `create_time`, `update_time`) VALUES (4, '14452526363', '$2a$10$5aTGKHRZ0Qaa2OeTSRNvZuR9QoRSBG5hPH4ZPe7V8bROeS0xSDsm2', '测试', NULL, 0, 'user', 0, NULL, 'eighteenthstuai@gmail.com', 0.00, 0.00, 600, 1, 0, '踩踩踩', NULL, '2026-02-04 15:48:03', '127.0.0.1', 'web', '2026-02-04 15:46:03', '2026-02-04 16:09:23');


-- ----------------------------
-- Table structure for sys_user_oauth
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_oauth`;
CREATE TABLE `sys_user_oauth`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NULL DEFAULT NULL COMMENT '关联用户ID (首次登录可能为空)',
  `oauth_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '第三方类型: wechat_mp(微信小程序), wechat_app(微信APP), alipay',
  `openid` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '第三方openid',
  `unionid` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微信unionid (多端打通用)',
  `nickname` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '第三方昵称',
  `avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '第三方头像',
  `gender` tinyint NULL DEFAULT 0 COMMENT '性别',
  `country` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '国家',
  `province` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '省份',
  `city` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '城市',
  `session_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微信session_key (加密存储)',
  `access_token` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'access_token',
  `refresh_token` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'refresh_token',
  `token_expire_time` datetime NULL DEFAULT NULL COMMENT 'token过期时间',
  `raw_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '原始数据JSON',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '首次授权时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_oauth`(`oauth_type` ASC, `openid` ASC) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_unionid`(`unionid` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '第三方登录绑定表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tf_bank_card
-- ----------------------------
DROP TABLE IF EXISTS `tf_bank_card`;
CREATE TABLE `tf_bank_card`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `bank_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '银行名称',
  `card_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '卡号(加密存储)',
  `card_type` tinyint NULL DEFAULT 1 COMMENT '卡类型: 1借记卡, 2信用卡',
  `holder_name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '持卡人姓名',
  `is_default` tinyint NULL DEFAULT 0 COMMENT '是否默认',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 1正常, 0禁用',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '银行卡表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tf_commission
-- ----------------------------
DROP TABLE IF EXISTS `tf_commission`;
CREATE TABLE `tf_commission`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` bigint NOT NULL COMMENT '关联订单ID',
  `merchant_id` bigint NULL DEFAULT NULL COMMENT '商户ID',
  `merchant_user_id` bigint NOT NULL COMMENT '商户对应的用户ID',
  `stage_id` bigint NULL DEFAULT NULL COMMENT '关联阶段ID',
  `stage_type` int NULL DEFAULT NULL COMMENT '阶段类型: 1首付款 2尾款 3质保款',
  `original_amount` decimal(12, 2) NOT NULL COMMENT '原始金额（释放金额）',
  `commission_rate` decimal(5, 2) NOT NULL COMMENT '提成比例（百分比）',
  `commission_amount` decimal(12, 2) NOT NULL COMMENT '提成金额',
  `actual_amount` decimal(12, 2) NOT NULL COMMENT '商家实际到账金额',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单编号',
  `order_title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单标题',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `status` int NOT NULL DEFAULT 1 COMMENT '状态: 0待结算 1已结算',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order`(`order_id` ASC) USING BTREE,
  INDEX `idx_merchant`(`merchant_id` ASC) USING BTREE,
  INDEX `idx_merchant_user`(`merchant_user_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '平台提成记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tf_contract
-- ----------------------------
DROP TABLE IF EXISTS `tf_contract`;
CREATE TABLE `tf_contract`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL COMMENT '关联订单ID',
  `contract_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '合同编号',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '合同标题',
  `party_a_id` bigint NOT NULL COMMENT '甲方ID(用户)',
  `party_a_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '甲方名称',
  `party_b_id` bigint NOT NULL COMMENT '乙方ID(商家)',
  `party_b_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '乙方名称',
  `project_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '项目描述',
  `tech_requirements` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '技术要求',
  `delivery_standard` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '验收标准',
  `delivery_deadline` date NULL DEFAULT NULL COMMENT '交付期限',
  `total_amount` decimal(12, 2) NOT NULL COMMENT '合同总金额',
  `payment_terms` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '付款条款JSON(阶段明细)',
  `breach_clause` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '违约条款',
  `confidential_clause` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '保密协议',
  `ip_clause` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '知识产权归属',
  `dispute_clause` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '争议解决方式',
  `party_a_signature` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '甲方签名(Base64图片)',
  `party_a_sign_time` datetime NULL DEFAULT NULL COMMENT '甲方签署时间',
  `party_b_signature` mediumtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '乙方签名(Base64图片)',
  `party_b_sign_time` datetime NULL DEFAULT NULL COMMENT '乙方签署时间',
  `status` tinyint NULL DEFAULT 0 COMMENT '0草稿 1待甲方签署 2待乙方签署 3已生效 4已作废',
  `evidence_hash` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '存证哈希',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_contract_no`(`contract_no` ASC) USING BTREE,
  INDEX `idx_order`(`order_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '项目合同表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tf_dispute
-- ----------------------------
DROP TABLE IF EXISTS `tf_dispute`;
CREATE TABLE `tf_dispute`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL COMMENT '关联订单ID',
  `initiator_id` bigint NOT NULL COMMENT '发起人ID',
  `initiator_role` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '发起人角色: user/merchant',
  `reason` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '申诉原因',
  `evidence_urls` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '证据材料链接(JSON数组)',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态: 0待处理, 1处理中, 2已裁决, 3已撤销',
  `result` tinyint NULL DEFAULT NULL COMMENT '裁决结果: 1甲方胜, 2乙方胜, 3各打五十大板',
  `result_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '裁决说明',
  `admin_id` bigint NULL DEFAULT NULL COMMENT '处理管理员ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `resolved_time` datetime NULL DEFAULT NULL COMMENT '裁决时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order`(`order_id` ASC) USING BTREE,
  INDEX `idx_initiator`(`initiator_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '纠纷表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tf_dispute_message
-- ----------------------------
DROP TABLE IF EXISTS `tf_dispute_message`;
CREATE TABLE `tf_dispute_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `dispute_id` bigint NOT NULL COMMENT '纠纷ID',
  `user_id` bigint NOT NULL COMMENT '发送人ID',
  `role` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色: user/merchant/admin',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '留言内容',
  `attachments` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '附件链接(JSON数组)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_dispute`(`dispute_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '纠纷留言表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tf_evidence
-- ----------------------------
DROP TABLE IF EXISTS `tf_evidence`;
CREATE TABLE `tf_evidence`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL COMMENT '关联订单ID',
  `stage_id` bigint NULL DEFAULT NULL COMMENT '关联阶段ID',
  `type` tinyint NOT NULL COMMENT '类型: 1合同签署, 2资金托管, 3交付提交, 4验收确认, 5纠纷证据',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '存证标题',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '存证内容描述',
  `file_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件URL',
  `file_hash` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件哈希',
  `block_height` bigint NULL DEFAULT NULL COMMENT '区块高度',
  `chain_time` datetime NULL DEFAULT NULL COMMENT '上链时间',
  `operator_id` bigint NOT NULL COMMENT '操作人ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order`(`order_id` ASC) USING BTREE,
  INDEX `idx_file_hash`(`file_hash` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 34 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '存证记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tf_merchant
-- ----------------------------
DROP TABLE IF EXISTS `tf_merchant`;
CREATE TABLE `tf_merchant`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '商户ID',
  `user_id` bigint NOT NULL COMMENT '关联用户ID',
  `merchant_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商户编号',
  `merchant_type` tinyint NOT NULL DEFAULT 1 COMMENT '商户类型: 1个体工商户 2企业/组织',
  `shop_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '店铺名称',
  `company_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '企业/组织名称（企业类型必填）',
  `legal_person` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '法人/负责人姓名',
  `legal_id_card` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '法人身份证号（加密）',
  `license_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '营业执照号/统一社会信用代码',
  `license_img` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '营业执照图片URL',
  `id_card_front` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '身份证正面图片URL',
  `id_card_back` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '身份证反面图片URL',
  `other_docs` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '其他资质文档URL（JSON数组）',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系电话',
  `contact_email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系邮箱',
  `business_address` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '经营地址',
  `business_scope` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '经营范围',
  `business_categories` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '业务分类（多个用逗号分隔，如1,2,3）',
  `commission_rate` decimal(5, 2) NULL DEFAULT 5.00 COMMENT '平台提成比例（%）',
  `credit_score` int NULL DEFAULT 600 COMMENT '信用评分',
  `order_count` int NULL DEFAULT 0 COMMENT '完成订单数',
  `total_income` decimal(12, 2) NULL DEFAULT 0.00 COMMENT '累计收入',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 0待审核 1正常 2禁用',
  `verify_time` datetime NULL DEFAULT NULL COMMENT '认证时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_merchant_no`(`merchant_no` ASC) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_merchant_type`(`merchant_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tf_merchant_apply
-- ----------------------------
DROP TABLE IF EXISTS `tf_merchant_apply`;
CREATE TABLE `tf_merchant_apply`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `apply_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '申请编号',
  `user_id` bigint NOT NULL COMMENT '申请用户ID',
  `merchant_type` tinyint NOT NULL DEFAULT 1 COMMENT '商户类型: 1个体工商户 2企业/组织',
  `shop_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '店铺名称',
  `company_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '企业/组织名称',
  `legal_person` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '法人/负责人姓名',
  `legal_id_card` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '法人身份证号（加密）',
  `license_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '营业执照号/统一社会信用代码',
  `license_img` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '营业执照图片URL',
  `id_card_front` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '身份证正面图片URL',
  `id_card_back` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '身份证反面图片URL',
  `other_docs` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '其他资质文档URL（JSON数组）',
  `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '联系电话',
  `contact_email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '联系邮箱',
  `business_address` varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '经营地址',
  `business_scope` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '经营范围',
  `business_categories` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '业务分类',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态: 0待审核 1通过 2拒绝',
  `audit_user_id` bigint NULL DEFAULT NULL COMMENT '审核人ID',
  `audit_time` datetime NULL DEFAULT NULL COMMENT '审核时间',
  `audit_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核备注/拒绝原因',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_apply_no`(`apply_no` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商户申请表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tf_order
-- ----------------------------
DROP TABLE IF EXISTS `tf_order`;
CREATE TABLE `tf_order`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单编号',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单标题/项目名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '需求描述',
  `category_id` int NULL DEFAULT 5 COMMENT '分类: 1UI设计, 2前端开发, 3后端开发, 4小程序, 5其他',
  `tech_stack` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '技术栈要求 (如: Vue3, Java, MySQL)',
  `features` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '功能描述/需求详情',
  `doc_urls` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '开发文档/截图链接 (JSON数组)',
  `buyer_id` bigint NOT NULL COMMENT '买家ID (甲方)',
  `seller_id` bigint NULL DEFAULT NULL COMMENT '卖家ID (乙方)',
  `total_amount` decimal(12, 2) NOT NULL COMMENT '订单总金额',
  `deposit_amount` decimal(12, 2) NULL DEFAULT 0.00 COMMENT '已托管金额',
  `released_amount` decimal(12, 2) NULL DEFAULT 0.00 COMMENT '已释放金额',
  `status` int NULL DEFAULT 0 COMMENT '状态: 0待接单, 1待托管, 2进行中, 3待验收, 4已完成, 5已取消, 6纠纷中',
  `start_time` datetime NULL DEFAULT NULL COMMENT '项目开始时间',
  `delivery_time` datetime NULL DEFAULT NULL COMMENT '预计交付时间',
  `completed_time` datetime NULL DEFAULT NULL COMMENT '实际完成时间',
  `contract_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '电子合同URL',
  `contract_hash` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '合同存证哈希',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_buyer`(`buyer_id` ASC) USING BTREE,
  INDEX `idx_seller`(`seller_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_category`(`category_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单主表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tf_order_message
-- ----------------------------
DROP TABLE IF EXISTS `tf_order_message`;
CREATE TABLE `tf_order_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL COMMENT '订单ID',
  `sender_id` bigint NOT NULL COMMENT '发送者ID',
  `receiver_id` bigint NOT NULL COMMENT '接收者ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息内容',
  `type` tinyint NULL DEFAULT 1 COMMENT '类型: 1文本, 2图片, 3文件',
  `is_read` tinyint NULL DEFAULT 0 COMMENT '是否已读: 0否, 1是',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_receiver_read`(`receiver_id` ASC, `is_read` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tf_order_stage
-- ----------------------------
DROP TABLE IF EXISTS `tf_order_stage`;
CREATE TABLE `tf_order_stage`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `order_id` bigint NOT NULL COMMENT '关联订单ID',
  `stage_name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '阶段名称 (如: 启动金, 阶段一交付)',
  `amount` decimal(12, 2) NOT NULL COMMENT '该阶段金额',
  `status` tinyint NULL DEFAULT 0 COMMENT '状态: 0未开始, 1已托管, 2待验收, 3已验收/已结算, 4质保中, 5质保已释放',
  `stage_type` tinyint NULL DEFAULT 1 COMMENT '阶段类型: 1首付款, 2尾款, 3质保款',
  `sort` int NULL DEFAULT 1 COMMENT '排序',
  `evidence_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '交付物/证据链接',
  `evidence_hash` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '存证哈希',
  `delivery_desc` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '交付说明',
  `delivery_time` datetime NULL DEFAULT NULL COMMENT '交付时间',
  `accept_time` datetime NULL DEFAULT NULL COMMENT '验收时间',
  `release_time` datetime NULL DEFAULT NULL COMMENT '款项释放时间',
  `warranty_end_time` datetime NULL DEFAULT NULL COMMENT '质保期结束时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  `percent` int NULL DEFAULT 0 COMMENT '付款比例百分比',
  `milestone` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '付款节点',
  `risk_note` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '风险止损说明',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order`(`order_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 55 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单分阶段支付表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tf_risk_event
-- ----------------------------
DROP TABLE IF EXISTS `tf_risk_event`;
CREATE TABLE `tf_risk_event`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `event_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '风险类型',
  `risk_level` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '风险等级: high/medium/low',
  `user_id` bigint NULL DEFAULT NULL COMMENT '关联用户ID',
  `order_id` bigint NULL DEFAULT NULL COMMENT '关联订单ID',
  `dispute_id` bigint NULL DEFAULT NULL COMMENT '关联纠纷ID',
  `title` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '事件标题',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '事件描述',
  `amount` decimal(12, 2) NULL DEFAULT NULL COMMENT '涉及金额',
  `ip_address` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `user_agent` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户代理',
  `extra_data` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '额外数据JSON',
  `status` int NOT NULL DEFAULT 0 COMMENT '状态: 0待处理 1已处理 2已忽略 3已拦截',
  `processed_by` bigint NULL DEFAULT NULL COMMENT '处理人ID',
  `process_remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '处理备注',
  `process_time` datetime NULL DEFAULT NULL COMMENT '处理时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_event_type`(`event_type` ASC) USING BTREE,
  INDEX `idx_risk_level`(`risk_level` ASC) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_order`(`order_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '风险事件记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for tf_service_message
-- ----------------------------
DROP TABLE IF EXISTS `tf_service_message`;
CREATE TABLE `tf_service_message`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `user_id` bigint NOT NULL COMMENT '用户ID（发起咨询的用户）',
  `sender_id` bigint NOT NULL COMMENT '发送者ID',
  `sender_role` tinyint NOT NULL DEFAULT 1 COMMENT '发送者角色: 1用户 2管理员',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '消息内容',
  `type` tinyint NOT NULL DEFAULT 1 COMMENT '消息类型: 1文本 2图片 3文件',
  `is_read` tinyint NOT NULL DEFAULT 0 COMMENT '是否已读: 0未读 1已读',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_sender_id`(`sender_id` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '客服消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tf_transaction
-- ----------------------------
DROP TABLE IF EXISTS `tf_transaction`;
CREATE TABLE `tf_transaction`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `order_id` bigint NULL DEFAULT NULL COMMENT '关联订单ID',
  `stage_id` bigint NULL DEFAULT NULL COMMENT '关联阶段ID',
  `type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '类型: recharge充值, withdraw提现, deposit托管, release释放, refund退款',
  `amount` decimal(12, 2) NOT NULL COMMENT '金额',
  `balance_after` decimal(12, 2) NULL DEFAULT NULL COMMENT '交易后余额',
  `title` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '交易摘要',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '备注',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态: 0处理中, 1成功, 2失败',
  `evidence_hash` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '存证哈希',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_order`(`order_id` ASC) USING BTREE,
  INDEX `idx_type`(`type` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 33 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '交易记录表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
