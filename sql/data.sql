USE `campus_book_share`;

SET NAMES utf8mb4;

INSERT INTO `user` (`id`, `username`, `password`, `phone`, `address`, `college`, `major`, `grade`, `points`, `credit_score`, `role`, `status`)
VALUES
  (1, 'admin', '$2a$10$zNC2N6uxZ6wM5FX9oRpQ8e6tRKankHJdTvVpiR0.RJu2qFMYBSjia', '13800000000', '校园管理办公室', '信息工程学院', '信息管理与信息系统', '教师', 200, 100, 'ADMIN', 1),
  (2, 'zhangsan', '$2a$10$zNC2N6uxZ6wM5FX9oRpQ8e6tRKankHJdTvVpiR0.RJu2qFMYBSjia', '13800000001', '南校区3号宿舍楼', '计算机学院', '计算机科学与技术', '2022级', 120, 98, 'USER', 1),
  (3, 'lisi', '$2a$10$zNC2N6uxZ6wM5FX9oRpQ8e6tRKankHJdTvVpiR0.RJu2qFMYBSjia', '13800000002', '图书馆附近', '软件学院', '软件工程', '2023级', 95, 97, 'USER', 1),
  (4, 'wangwu', '$2a$10$zNC2N6uxZ6wM5FX9oRpQ8e6tRKankHJdTvVpiR0.RJu2qFMYBSjia', '13800000003', '计算机学院楼', '网络空间安全学院', '网络工程', '2021级', 140, 99, 'USER', 1);

INSERT INTO `book_category` (`id`, `name`, `description`)
VALUES
  (1, '教材', '课程配套教材'),
  (2, '教辅资料', '配套练习与教辅'),
  (3, '考试资料', '等级考试与课程考试资料'),
  (4, '考研资料', '考研复习资料'),
  (5, '课外读物', '人文社科与兴趣阅读');

INSERT INTO `book` (`id`, `owner_id`, `original_owner_id`, `category_id`, `title`, `author`, `publisher`, `course_name`, `major`, `condition_level`, `book_location`, `cover_url`, `price`, `share_type`, `status`, `circulation_count`, `description`)
VALUES
  (1, 2, 2, 1, 'Java程序设计', '耿祥义', '清华大学出版社', 'Java程序设计', '计算机科学与技术', '九成新', '计算机学院楼', NULL, 18.00, 'SALE', 'ON_SHELF', 1, '适合 Java 课程学习，附少量课堂笔记。'),
  (2, 2, 2, 1, '操作系统', '汤小丹', '西安电子科技大学出版社', '操作系统', '计算机科学与技术', '八成新', '计算机学院实验楼', NULL, 15.00, 'SALE', 'ON_SHELF', 0, '页边有重点标注。'),
  (3, 3, 3, 1, '数据结构', '严蔚敏', '清华大学出版社', '数据结构', '软件工程', '九成新', '计算机学院楼', NULL, 20.00, 'SALE', 'ON_SHELF', 2, '刷题和复习常用教材。'),
  (4, 3, 3, 1, '数据库系统概论', '王珊', '高等教育出版社', '数据库原理', '软件工程', '九成新', '图书馆一楼', NULL, 0.00, 'BORROW', 'ON_SHELF', 1, '可借阅两周，保持书籍整洁。'),
  (5, 4, 4, 1, '软件工程导论', '张海藩', '清华大学出版社', '软件工程', '软件工程', '八成新', '软件学院楼', NULL, 0.00, 'BORROW', 'ON_SHELF', 0, '适合期末复习。'),
  (6, 2, 2, 1, '离散数学', '屈婉玲', '高等教育出版社', '离散数学', '计算机类', '八成新', '南校区宿舍区', NULL, 0.00, 'BORROW', 'ON_SHELF', 3, '可短期借阅，建议一周内归还。'),
  (7, 4, 4, 1, '计算机网络', '谢希仁', '电子工业出版社', '计算机网络', '网络工程', '九成新', '网络空间安全学院楼', NULL, 0.00, 'EXCHANGE', 'ON_SHELF', 1, '希望交换网络安全或路由相关教材。'),
  (8, 3, 3, 4, '考研英语词汇', '新东方考试研究中心', '群言出版社', '考研英语', '不限', '七成新', '南校区自习室', NULL, 0.00, 'EXCHANGE', 'ON_SHELF', 2, '希望交换数学或政治资料。'),
  (9, 2, 2, 1, 'Python程序设计', '董付国', '清华大学出版社', 'Python程序设计', '计算机类', '九成新', '计算机学院楼', NULL, 0.00, 'EXCHANGE', 'ON_SHELF', 0, '想换算法设计或机器学习入门书籍。'),
  (10, 2, 2, 1, '高等数学', '同济大学数学系', '高等教育出版社', '高等数学', '公共课', '七成新', '南校区宿舍区', NULL, 0.00, 'DONATE', 'ON_SHELF', 4, '适合新生使用，免费捐赠。'),
  (11, 3, 3, 3, '大学英语四级真题', '新东方考试研究中心', '浙江教育出版社', '大学英语', '公共课', '八成新', '图书馆二楼', NULL, 0.00, 'DONATE', 'ON_SHELF', 1, '备考四级可直接领取。'),
  (12, 4, 4, 4, '考研政治核心考案', '徐涛', '中国政法大学出版社', '考研政治', '不限', '八成新', '南校区自习室', NULL, 0.00, 'DONATE', 'ON_SHELF', 2, '考研资料捐赠给下一届同学。');

INSERT INTO `book_order` (`id`, `book_id`, `owner_id`, `applicant_id`, `exchange_book_id`, `order_type`, `status`, `remark`, `create_time`, `finish_time`)
VALUES
  (1, 1, 2, 3, NULL, 'SALE', 'COMPLETED', '线下宿舍楼下自提', '2026-04-10 10:00:00', '2026-04-10 18:30:00'),
  (2, 4, 3, 2, NULL, 'BORROW', 'COMPLETED', '借阅两周后归还', '2026-04-15 09:20:00', '2026-04-29 20:00:00'),
  (3, 7, 4, 2, 9, 'EXCHANGE', 'ACCEPTED', '约在图书馆完成交换', '2026-05-02 14:00:00', NULL),
  (4, 10, 2, 4, NULL, 'DONATE', 'COMPLETED', '捐赠给大一新生', '2026-03-20 08:30:00', '2026-03-20 12:00:00');

INSERT INTO `borrow_record` (`id`, `order_id`, `book_id`, `lender_id`, `borrower_id`, `borrow_time`, `expected_return_time`, `actual_return_time`, `status`)
VALUES
  (1, 2, 4, 3, 2, '2026-04-15 09:30:00', '2026-04-29 18:00:00', '2026-04-29 19:40:00', 'RETURNED');

INSERT INTO `circulation_record` (`id`, `book_id`, `from_user_id`, `to_user_id`, `circulation_type`, `order_id`, `remark`, `create_time`)
VALUES
  (1, 1, 2, 3, 'SALE', 1, 'Java程序设计已完成出售共享', '2026-04-10 18:30:00'),
  (2, 4, 3, 2, 'BORROW', 2, '数据库系统概论借出后已归还', '2026-04-29 19:40:00'),
  (3, 10, 2, 4, 'DONATE', 4, '高等数学完成捐赠共享', '2026-03-20 12:00:00'),
  (4, 10, 4, 4, 'RESHARE', NULL, '受赠者后续可再次共享，保留再次共享轨迹示例', '2026-04-18 16:20:00');

INSERT INTO `points_record` (`id`, `user_id`, `points_change`, `source_type`, `description`, `create_time`)
VALUES
  (1, 2, 20, 'DONATE_GIVER', '捐赠高等数学获得积分', '2026-03-20 12:10:00'),
  (2, 3, 10, 'SHARE_COMPLETED', '完成出售共享获得积分', '2026-04-10 18:40:00'),
  (3, 4, 8, 'DONATE_RECEIVER', '领取捐赠书籍确认后获得积分', '2026-03-20 12:15:00');

INSERT INTO `evaluation` (`id`, `order_id`, `evaluator_id`, `target_user_id`, `book_score`, `user_score`, `content`, `create_time`)
VALUES
  (1, 1, 3, 2, 5, 5, '书籍保存很好，交易沟通顺畅。', '2026-04-10 19:00:00'),
  (2, 2, 2, 3, 5, 5, '借阅流程清晰，按时归还。', '2026-04-29 20:10:00');

INSERT INTO `report` (`id`, `reporter_id`, `target_type`, `target_id`, `reason`, `status`, `create_time`, `handle_time`)
VALUES
  (1, 2, 'BOOK', 8, '信息描述过于简单，建议补充版本信息。', 'PROCESSED', '2026-04-22 11:00:00', '2026-04-22 16:30:00');
