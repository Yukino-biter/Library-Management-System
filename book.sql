-- =============================================
-- 书城管理系统 数据库脚本
-- 数据库：bookshop
-- 作者：黄淮学院 计算机与人工智能学院
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS bookshop DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用数据库
USE bookshop;

-- =============================================
-- 用户表 user
-- =============================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id`       INT          NOT NULL AUTO_INCREMENT COMMENT '用户ID，主键自增',
    `username` VARCHAR(50)  NOT NULL COMMENT '账号（唯一）',
    `password` VARCHAR(100) NOT NULL COMMENT '密码（BCrypt加密）',
    `nickname` VARCHAR(50)  NOT NULL COMMENT '昵称',
    `role`     VARCHAR(20)  NOT NULL DEFAULT 'user' COMMENT '角色：admin/user',
    `create_time` DATETIME  NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- =============================================
-- 图书表 book
-- =============================================
DROP TABLE IF EXISTS `book`;
CREATE TABLE `book` (
    `id`          INT            NOT NULL AUTO_INCREMENT COMMENT '图书ID，主键自增',
    `book_name`   VARCHAR(100)   NOT NULL COMMENT '图书名称',
    `author`      VARCHAR(50)    NOT NULL COMMENT '作者',
    `publisher`   VARCHAR(50)    DEFAULT NULL COMMENT '出版社',
    `isbn`        VARCHAR(20)    DEFAULT NULL COMMENT 'ISBN编号',
    `price`       DECIMAL(10,2)  NOT NULL COMMENT '价格',
    `category`    VARCHAR(30)    DEFAULT NULL COMMENT '图书分类',
    `stock`       INT            NOT NULL DEFAULT 0 COMMENT '库存数量',
    `description` TEXT           DEFAULT NULL COMMENT '图书简介',
    `image`       VARCHAR(255)   DEFAULT NULL COMMENT '图书封面图片路径',
    `create_time` DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书表';

-- =============================================
-- 插入测试数据
-- =============================================

-- 插入用户数据（密码均为 123456 的BCrypt加密值）
INSERT INTO `user` (`username`, `password`, `nickname`, `role`) VALUES
('admin', '$2a$10$THUiaPn8LObcMiD6lqVQSuIsSxsvUWYajA5BJ2QYAR.UnpRIBY97m', '管理员', 'admin'),
('zhangsan', '$2a$10$THUiaPn8LObcMiD6lqVQSuIsSxsvUWYajA5BJ2QYAR.UnpRIBY97m', '张三', 'user'),
('lisi', '$2a$10$THUiaPn8LObcMiD6lqVQSuIsSxsvUWYajA5BJ2QYAR.UnpRIBY97m', '李四', 'user');

-- 插入图书数据
INSERT INTO `book` (`book_name`, `author`, `publisher`, `isbn`, `price`, `category`, `stock`, `description`, `image`) VALUES
('Java核心技术', '凯·霍斯特曼', '机械工业出版社', '978-7-111-54742-6', 119.00, '计算机', 50, 'Java经典入门教材，涵盖Java基础语法、面向对象、集合框架、多线程等核心内容。', '/images/java_core.jpg'),
('深入理解计算机系统', '兰德尔·布莱恩特', '机械工业出版社', '978-7-111-54493-7', 139.00, '计算机', 30, '计算机科学经典教材，从程序员角度深入讲解计算机系统的底层原理。', '/images/csapp.jpg'),
('算法导论', '托马斯·科尔曼', '机械工业出版社', '978-7-111-40701-0', 128.00, '计算机', 25, '算法领域权威教材，全面介绍各种算法设计策略与分析方法。', '/images/algorithms.jpg'),
('红楼梦', '曹雪芹', '人民文学出版社', '978-7-020-00220-4', 59.70, '文学', 100, '中国古典四大名著之首，描写贾宝玉、林黛玉等人的悲欢离合。', '/images/honglou.jpg'),
('百年孤独', '加西亚·马尔克斯', '南海出版公司', '978-7-544-25346-0', 55.00, '文学', 80, '魔幻现实主义文学代表作，讲述布恩迪亚家族七代人的故事。', '/images/gudujbainian.jpg'),
('三体', '刘慈欣', '重庆出版社', '978-7-536-69293-0', 93.00, '科幻', 60, '中国科幻文学里程碑之作，描绘地球文明与三体文明的碰撞。', '/images/santi.jpg'),
('小王子', '安托万·圣埃克苏佩里', '人民文学出版社', '978-7-020-04232-3', 32.00, '文学', 120, '一部写给大人的童话，讲述来自小星球的小王子的奇妙旅行。', '/images/xiaowangzi.jpg'),
('数据库系统概论', '王珊', '高等教育出版社', '978-7-040-40664-8', 45.00, '计算机', 40, '数据库经典教材，系统讲解数据库系统的基本原理和设计方法。', '/images/database.jpg');

-- =============================================
-- 借阅记录表 borrow_record
-- =============================================
CREATE TABLE IF NOT EXISTS `borrow_record` (
    `id`          INT      NOT NULL AUTO_INCREMENT COMMENT '记录ID，主键自增',
    `user_id`     INT      NOT NULL COMMENT '借阅用户ID',
    `book_id`     INT      NOT NULL COMMENT '图书ID',
    `borrow_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '借阅时间',
    `return_time` DATETIME DEFAULT NULL COMMENT '归还时间（NULL表示未归还）',
    `status`      VARCHAR(20) NOT NULL DEFAULT 'borrowed' COMMENT '状态：borrowed-借阅中, returned-已归还',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='借阅记录表';
