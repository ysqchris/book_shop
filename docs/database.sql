-- book_shop二手书买卖平台数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS used_book_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE used_book_db;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    real_name VARCHAR(50) COMMENT '真实姓名',
    role TINYINT DEFAULT 0 COMMENT '角色(0-普通用户,1-管理员)',
    status TINYINT DEFAULT 0 COMMENT '状态(0-正常,1-禁用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记(0-未删除,1-已删除)',
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 分类表
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分类ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父分类ID(0表示顶级分类)',
    level TINYINT DEFAULT 1 COMMENT '分类级别(1-一级分类,2-二级分类)',
    sort_order INT DEFAULT 0 COMMENT '排序权重',
    icon VARCHAR(200) COMMENT '分类图标',
    description VARCHAR(500) COMMENT '分类描述',
    status TINYINT DEFAULT 0 COMMENT '状态(0-正常,1-禁用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记',
    INDEX idx_parent_id (parent_id),
    INDEX idx_sort_order (sort_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='分类表';

-- 图书表
CREATE TABLE IF NOT EXISTS books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '图书ID',
    title VARCHAR(200) NOT NULL COMMENT '图书标题',
    author VARCHAR(100) NOT NULL COMMENT '作者',
    isbn VARCHAR(20) COMMENT 'ISBN',
    publisher VARCHAR(100) COMMENT '出版社',
    publish_date VARCHAR(20) COMMENT '出版日期',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    original_price DECIMAL(10,2) COMMENT '原价',
    stock INT DEFAULT 0 COMMENT '库存数量',
    cover_image VARCHAR(500) COMMENT '封面图片',
    description TEXT COMMENT '图书描述',
    status TINYINT DEFAULT 0 COMMENT '状态(0-上架,1-下架)',
    seller_id BIGINT NOT NULL COMMENT '卖家用户ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记',
    INDEX idx_category_id (category_id),
    INDEX idx_seller_id (seller_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time),
    INDEX idx_price (price),
    FULLTEXT INDEX idx_title_author (title, author)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书表';

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    pay_amount DECIMAL(10,2) NOT NULL COMMENT '实际支付金额',
    status TINYINT DEFAULT 0 COMMENT '订单状态(0-待支付,1-已支付,2-已发货,3-已完成,4-已取消)',
    pay_type TINYINT COMMENT '支付方式(0-支付宝,1-微信,2-银行卡)',
    pay_time DATETIME COMMENT '支付时间',
    ship_time DATETIME COMMENT '发货时间',
    receiver_name VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    receiver_phone VARCHAR(20) NOT NULL COMMENT '收货人电话',
    receiver_address VARCHAR(500) NOT NULL COMMENT '收货地址',
    remark VARCHAR(500) COMMENT '订单备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记',
    INDEX idx_user_id (user_id),
    INDEX idx_order_no (order_no),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 订单明细表
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单明细ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    book_id BIGINT NOT NULL COMMENT '图书ID',
    book_title VARCHAR(200) NOT NULL COMMENT '图书标题',
    book_cover VARCHAR(500) COMMENT '图书封面',
    book_price DECIMAL(10,2) NOT NULL COMMENT '图书单价',
    quantity INT NOT NULL COMMENT '购买数量',
    subtotal DECIMAL(10,2) NOT NULL COMMENT '小计金额',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除标记',
    INDEX idx_order_id (order_id),
    INDEX idx_book_id (book_id),
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (book_id) REFERENCES books(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- 插入初始数据
-- 插入管理员用户
INSERT INTO users (username, password, email, real_name, role) VALUES 
('admin', MD5('admin123'), 'admin@bookshop.com', '系统管理员', 1);

-- 插入分类数据
INSERT INTO categories (name, parent_id, level, sort_order, description) VALUES 
('文学小说', 0, 1, 1, '文学类小说书籍'),
('科学技术', 0, 1, 2, '科学技术类书籍'),
('教材教辅', 0, 1, 3, '教材和教辅书籍'),
('生活艺术', 0, 1, 4, '生活艺术类书籍'),
('儿童读物', 0, 1, 5, '儿童读物书籍'),
('中国文学', 1, 2, 1, '中国文学小说'),
('外国文学', 1, 2, 2, '外国文学小说'),
('计算机', 2, 2, 1, '计算机技术书籍'),
('医学', 2, 2, 2, '医学类书籍'),
('大学教材', 3, 2, 1, '大学教材书籍'),
('高中教材', 3, 2, 2, '高中教材书籍');

-- 插入示例图书数据（余华经典书单 + 其他样板书）
INSERT INTO books (title, author, isbn, publisher, publish_date, category_id, price, original_price, stock, cover_image, description, seller_id) VALUES 
-- 余华经典书单
('活着', '余华', '9787506365437', '作家出版社', '2012-08-01', 6, 22.00, 35.00, 50, '/covers/1.svg', '《活着》是余华的代表作之一，讲述了农村人福贵悲惨的人生遭遇。福贵本是个阔少爷，可他嗜赌如命，终于在一夜之间将家产输得精光，父亲因此被气死，母亲则在穷困中患了重病，妻子家珍含辛茹苦带大了一双儿女，但女儿不幸变成了哑巴，儿子机灵活泼……然而，真正的悲剧从此才开始渐次上演。这是一部关于生命与死亡、苦难与坚韧的史诗级作品。', 1),
('许三观卖血记', '余华', '9787506366960', '作家出版社', '2013-03-01', 6, 25.00, 39.00, 40, '/covers/2.svg', '《许三观卖血记》是余华的长篇小说，以博大的温情描绘了磨难中的人生，以激烈的故事形式表达了人在面对厄运时求生的欲望。许三观是城里丝厂的送茧工，他靠着卖血渡过了人生的一个个难关——娶妻、还债、度荒年、救儿子……这是一部关于父爱、家庭与生命尊严的感人之作。', 1),
('兄弟', '余华', '9787506362641', '作家出版社', '2012-03-01', 6, 39.00, 59.00, 30, '/covers/3.svg', '《兄弟》是余华的长篇小说，讲述了江南小镇两兄弟李光头和宋钢的人生故事。小说分为上下两册，上册描写了文化大革命时期的苦难，下册则描写了改革开放后的荒诞现实。这是一部关于中国近现代历史变迁、人性善恶与兄弟情义的宏大叙事。', 1),
('在细雨中呼喊', '余华', '9787506366977', '作家出版社', '2013-03-01', 6, 22.00, 32.00, 35, '/covers/4.svg', '《在细雨中呼喊》是余华的第一部长篇小说，以一个孩子的视角，描述了一个江南小镇上的童年往事。小说充满了对童年的追忆与反思，展现了成长过程中的孤独、恐惧与渴望。这是一部关于记忆、时间与成长的诗意之作，被誉为余华最具文学价值的作品之一。', 1),
('第七天', '余华', '9787536075627', '新星出版社', '2013-06-01', 6, 28.00, 39.80, 45, '/covers/5.svg', '《第七天》是余华2013年出版的长篇小说，以荒诞的笔触描绘了一个死去的人在七天里的游历。主人公杨飞死后游荡在现实与死亡之间，遇见了各种各样的亡灵，拼凑出一幅当代中国社会的众生相。这是一部关于生死、爱情与社会现实的深刻反思之作。', 1),
('文城', '余华', '9787530222690', '北京十月文艺出版社', '2021-03-01', 6, 49.50, 59.00, 60, '/covers/6.svg', '《文城》是余华2021年出版的长篇小说，讲述了清末民初年间，一个叫林祥福的男人，带着一块土地契约，千里寻妻的故事。小说以宏大的历史背景为底色，描绘了那个动荡年代里普通人的命运与情感。这是余华时隔多年后的重磅回归之作，延续了他一贯的史诗风格。', 1),
-- 其他样板书
('三体', '刘慈欣', '9787536692930', '重庆出版社', '2008-01-01', 6, 25.00, 45.00, 10, '/covers/7.svg', '《三体》是刘慈欣创作的系列科幻小说，讲述了地球文明与三体文明的信息交流、生死搏杀及两个文明在宇宙中的兴衰历程。', 1),
('Java编程思想', 'Bruce Eckel', '9787111213826', '机械工业出版社', '2007-06-01', 8, 45.00, 89.00, 8, '/covers/8.svg', 'Java编程经典教材，深入讲解Java语言的核心概念与编程思想。', 1),
('高等数学', '同济大学数学系', '9787040396638', '高等教育出版社', '2014-07-01', 10, 30.00, 65.00, 20, '/covers/9.svg', '大学高等数学教材，全国高校广泛使用的经典教材。', 1);