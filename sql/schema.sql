CREATE DATABASE IF NOT EXISTS `campus_book_share`
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE `campus_book_share`;

SET NAMES utf8mb4;

DROP TABLE IF EXISTS `report`;
DROP TABLE IF EXISTS `evaluation`;
DROP TABLE IF EXISTS `points_record`;
DROP TABLE IF EXISTS `circulation_record`;
DROP TABLE IF EXISTS `borrow_record`;
DROP TABLE IF EXISTS `book_order`;
DROP TABLE IF EXISTS `book`;
DROP TABLE IF EXISTS `book_category`;
DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `college` VARCHAR(100) DEFAULT NULL COMMENT '学院',
  `major` VARCHAR(100) DEFAULT NULL COMMENT '专业',
  `grade` VARCHAR(50) DEFAULT NULL COMMENT '年级',
  `points` INT NOT NULL DEFAULT 0 COMMENT '积分',
  `credit_score` INT NOT NULL DEFAULT 100 COMMENT '信用分',
  `role` VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色 USER/ADMIN',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 1启用 0禁用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

CREATE TABLE `book_category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '分类描述',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_book_category_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='书籍分类表';

CREATE TABLE `book` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `owner_id` BIGINT NOT NULL COMMENT '当前持有人/发布者',
  `original_owner_id` BIGINT NOT NULL COMMENT '初始发布者',
  `category_id` BIGINT NOT NULL COMMENT '分类ID',
  `title` VARCHAR(150) NOT NULL COMMENT '书名',
  `author` VARCHAR(100) DEFAULT NULL COMMENT '作者',
  `publisher` VARCHAR(100) DEFAULT NULL COMMENT '出版社',
  `course_name` VARCHAR(100) DEFAULT NULL COMMENT '课程名称',
  `major` VARCHAR(100) DEFAULT NULL COMMENT '适用专业',
  `condition_level` VARCHAR(30) DEFAULT NULL COMMENT '成色等级',
  `cover_url` VARCHAR(255) DEFAULT NULL COMMENT '封面地址',
  `price` DECIMAL(10,2) DEFAULT NULL COMMENT '价格',
  `share_type` VARCHAR(20) NOT NULL COMMENT '共享方式 SALE/BORROW/EXCHANGE/DONATE',
  `status` VARCHAR(20) NOT NULL DEFAULT 'ON_SHELF' COMMENT '状态',
  `circulation_count` INT NOT NULL DEFAULT 0 COMMENT '流转次数',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '描述',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_book_owner_id` (`owner_id`),
  KEY `idx_book_original_owner_id` (`original_owner_id`),
  KEY `idx_book_category_id` (`category_id`),
  CONSTRAINT `fk_book_owner_id` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_book_original_owner_id` FOREIGN KEY (`original_owner_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_book_category_id` FOREIGN KEY (`category_id`) REFERENCES `book_category` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='书籍表';

CREATE TABLE `book_order` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `book_id` BIGINT NOT NULL COMMENT '目标书籍ID',
  `owner_id` BIGINT NOT NULL COMMENT '书籍所有者ID',
  `applicant_id` BIGINT NOT NULL COMMENT '申请人ID',
  `exchange_book_id` BIGINT DEFAULT NULL COMMENT '交换书籍ID',
  `order_type` VARCHAR(20) NOT NULL COMMENT '订单类型',
  `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '订单状态',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `finish_time` DATETIME DEFAULT NULL COMMENT '完成时间',
  PRIMARY KEY (`id`),
  KEY `idx_book_order_book_id` (`book_id`),
  KEY `idx_book_order_owner_id` (`owner_id`),
  KEY `idx_book_order_applicant_id` (`applicant_id`),
  KEY `idx_book_order_exchange_book_id` (`exchange_book_id`),
  CONSTRAINT `fk_book_order_book_id` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`),
  CONSTRAINT `fk_book_order_owner_id` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_book_order_applicant_id` FOREIGN KEY (`applicant_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_book_order_exchange_book_id` FOREIGN KEY (`exchange_book_id`) REFERENCES `book` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

CREATE TABLE `borrow_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `book_id` BIGINT NOT NULL COMMENT '书籍ID',
  `lender_id` BIGINT NOT NULL COMMENT '出借人ID',
  `borrower_id` BIGINT NOT NULL COMMENT '借阅人ID',
  `borrow_time` DATETIME NOT NULL COMMENT '借出时间',
  `expected_return_time` DATETIME DEFAULT NULL COMMENT '预计归还时间',
  `actual_return_time` DATETIME DEFAULT NULL COMMENT '实际归还时间',
  `status` VARCHAR(20) NOT NULL DEFAULT 'BORROWING' COMMENT '借阅状态',
  PRIMARY KEY (`id`),
  KEY `idx_borrow_record_order_id` (`order_id`),
  KEY `idx_borrow_record_book_id` (`book_id`),
  KEY `idx_borrow_record_lender_id` (`lender_id`),
  KEY `idx_borrow_record_borrower_id` (`borrower_id`),
  CONSTRAINT `fk_borrow_record_order_id` FOREIGN KEY (`order_id`) REFERENCES `book_order` (`id`),
  CONSTRAINT `fk_borrow_record_book_id` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`),
  CONSTRAINT `fk_borrow_record_lender_id` FOREIGN KEY (`lender_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_borrow_record_borrower_id` FOREIGN KEY (`borrower_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='借阅记录表';

CREATE TABLE `circulation_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `book_id` BIGINT NOT NULL COMMENT '书籍ID',
  `from_user_id` BIGINT DEFAULT NULL COMMENT '流出用户ID',
  `to_user_id` BIGINT DEFAULT NULL COMMENT '流入用户ID',
  `circulation_type` VARCHAR(20) NOT NULL COMMENT '流转类型',
  `order_id` BIGINT DEFAULT NULL COMMENT '关联订单ID',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_circulation_record_book_id` (`book_id`),
  KEY `idx_circulation_record_from_user_id` (`from_user_id`),
  KEY `idx_circulation_record_to_user_id` (`to_user_id`),
  KEY `idx_circulation_record_order_id` (`order_id`),
  CONSTRAINT `fk_circulation_record_book_id` FOREIGN KEY (`book_id`) REFERENCES `book` (`id`),
  CONSTRAINT `fk_circulation_record_from_user_id` FOREIGN KEY (`from_user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_circulation_record_to_user_id` FOREIGN KEY (`to_user_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_circulation_record_order_id` FOREIGN KEY (`order_id`) REFERENCES `book_order` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='书籍流转记录表';

CREATE TABLE `points_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `points_change` INT NOT NULL COMMENT '积分变动',
  `source_type` VARCHAR(50) NOT NULL COMMENT '来源类型',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '积分描述',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_points_record_user_id` (`user_id`),
  CONSTRAINT `fk_points_record_user_id` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='积分记录表';

CREATE TABLE `evaluation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `order_id` BIGINT NOT NULL COMMENT '订单ID',
  `evaluator_id` BIGINT NOT NULL COMMENT '评价人ID',
  `target_user_id` BIGINT NOT NULL COMMENT '被评价人ID',
  `book_score` TINYINT NOT NULL COMMENT '书籍评分',
  `user_score` TINYINT NOT NULL COMMENT '用户评分',
  `content` VARCHAR(500) DEFAULT NULL COMMENT '评价内容',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_evaluation_order_id` (`order_id`),
  KEY `idx_evaluation_evaluator_id` (`evaluator_id`),
  KEY `idx_evaluation_target_user_id` (`target_user_id`),
  CONSTRAINT `fk_evaluation_order_id` FOREIGN KEY (`order_id`) REFERENCES `book_order` (`id`),
  CONSTRAINT `fk_evaluation_evaluator_id` FOREIGN KEY (`evaluator_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_evaluation_target_user_id` FOREIGN KEY (`target_user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='评价表';

CREATE TABLE `report` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `reporter_id` BIGINT NOT NULL COMMENT '举报人ID',
  `target_type` VARCHAR(20) NOT NULL COMMENT '举报目标类型',
  `target_id` BIGINT NOT NULL COMMENT '目标ID',
  `reason` VARCHAR(500) NOT NULL COMMENT '举报原因',
  `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '处理状态',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `handle_time` DATETIME DEFAULT NULL COMMENT '处理时间',
  PRIMARY KEY (`id`),
  KEY `idx_report_reporter_id` (`reporter_id`),
  CONSTRAINT `fk_report_reporter_id` FOREIGN KEY (`reporter_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='举报表';
