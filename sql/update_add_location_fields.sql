USE `campus_book_share`;

SET NAMES utf8mb4;

-- 增量补丁说明：
-- 1. 本脚本用于已运行过旧版本项目的数据库，补充 user.address 和 book.book_location 字段。
-- 2. 不会删除原有数据，也不需要重建数据库。
-- 3. 脚本通过 information_schema 判断字段是否存在，重复执行时不会因为字段已存在而中断。
-- 4. 如果你手动改成普通 ALTER TABLE 后提示 Duplicate column name，说明字段已经存在，
--    可以忽略 ALTER TABLE 部分，继续执行 UPDATE 语句。

SET @user_address_exists = (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = 'campus_book_share'
      AND TABLE_NAME = 'user'
      AND COLUMN_NAME = 'address'
);

SET @user_address_sql = IF(
    @user_address_exists = 0,
    'ALTER TABLE `user` ADD COLUMN `address` VARCHAR(255) DEFAULT NULL COMMENT ''用户常用联系地址''',
    'SELECT ''user.address already exists'' '
);

PREPARE stmt_user_address FROM @user_address_sql;
EXECUTE stmt_user_address;
DEALLOCATE PREPARE stmt_user_address;

SET @book_location_exists = (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = 'campus_book_share'
      AND TABLE_NAME = 'book'
      AND COLUMN_NAME = 'book_location'
);

SET @book_location_sql = IF(
    @book_location_exists = 0,
    'ALTER TABLE `book` ADD COLUMN `book_location` VARCHAR(255) DEFAULT NULL COMMENT ''书籍当前存放位置''',
    'SELECT ''book.book_location already exists'' '
);

PREPARE stmt_book_location FROM @book_location_sql;
EXECUTE stmt_book_location;
DEALLOCATE PREPARE stmt_book_location;

UPDATE `user`
SET `address` = CASE `username`
    WHEN 'admin' THEN '校园管理办公室'
    WHEN 'zhangsan' THEN '南校区3号宿舍楼'
    WHEN 'lisi' THEN '图书馆附近'
    WHEN 'wangwu' THEN '计算机学院楼'
    ELSE '南校区'
END
WHERE `address` IS NULL OR `address` = '';

UPDATE `book`
SET `book_location` = CASE
    WHEN `title` = 'Java程序设计' THEN '计算机学院楼'
    WHEN `title` = '数据库系统概论' THEN '图书馆一楼'
    WHEN `title` = '计算机网络' THEN '网络空间安全学院楼'
    WHEN `title` = '高等数学' THEN '南校区宿舍区'
    WHEN `title` = '操作系统' THEN '计算机学院实验楼'
    WHEN `title` = '软件工程导论' THEN '软件学院楼'
    WHEN `title` = '大学英语四级真题' THEN '图书馆二楼'
    WHEN `title` = '考研英语词汇' THEN '南校区自习室'
    WHEN `title` = '数据结构' THEN '计算机学院楼'
    WHEN `title` = '离散数学' THEN '南校区宿舍区'
    WHEN `title` = '考研政治核心考案' THEN '南校区自习室'
    WHEN `title` = 'Python程序设计' THEN '计算机学院楼'
    ELSE '南校区书籍共享点'
END
WHERE `book_location` IS NULL OR `book_location` = '';
