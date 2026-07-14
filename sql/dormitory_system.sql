SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

CREATE DATABASE IF NOT EXISTS dormitory_system
DEFAULT CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE dormitory_system;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS repair;
DROP TABLE IF EXISTS check_record;
DROP TABLE IF EXISTS student;
DROP TABLE IF EXISTS dormitory;
DROP TABLE IF EXISTS building;
DROP TABLE IF EXISTS `user`;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE `user` (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '登录名',
    password VARCHAR(100) NOT NULL COMMENT '加密密码',
    salt VARCHAR(20) COMMENT '密码盐值',
    real_name VARCHAR(50) NOT NULL COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    role VARCHAR(20) NOT NULL DEFAULT 'STUDENT' COMMENT 'ADMIN/BUILDING_ADMIN/STUDENT',
    status TINYINT DEFAULT 1 COMMENT '1启用 0禁用',
    create_time DATETIME DEFAULT NOW(),
    update_time DATETIME DEFAULT NOW() ON UPDATE NOW(),
    last_login_time DATETIME,
    last_login_ip VARCHAR(50),
    INDEX idx_username (username),
    INDEX idx_role (role),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户账号表';

CREATE TABLE building (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '楼栋ID',
    building_name VARCHAR(50) NOT NULL COMMENT '楼栋名称',
    floor_count INT DEFAULT 6 COMMENT '楼层总数',
    address VARCHAR(200) COMMENT '地址',
    building_admin_id INT COMMENT '楼栋管理员ID',
    status TINYINT DEFAULT 1 COMMENT '1正常 0停用',
    create_time DATETIME DEFAULT NOW(),
    update_time DATETIME DEFAULT NOW() ON UPDATE NOW(),
    FOREIGN KEY (building_admin_id) REFERENCES `user`(id) ON DELETE SET NULL,
    INDEX idx_admin (building_admin_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='楼栋表';

CREATE TABLE dormitory (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '宿舍ID',
    building_id INT NOT NULL COMMENT '所属楼栋ID',
    room_number VARCHAR(20) NOT NULL COMMENT '房间号',
    floor INT COMMENT '楼层',
    capacity INT DEFAULT 4 NOT NULL COMMENT '床位数',
    current_count INT DEFAULT 0 NOT NULL COMMENT '当前入住人数',
    status VARCHAR(20) DEFAULT 'AVAILABLE' COMMENT 'AVAILABLE/FULL/REPAIR',
    create_time DATETIME DEFAULT NOW(),
    update_time DATETIME DEFAULT NOW() ON UPDATE NOW(),
    UNIQUE KEY uk_building_room (building_id, room_number),
    FOREIGN KEY (building_id) REFERENCES building(id) ON DELETE CASCADE,
    INDEX idx_building (building_id),
    INDEX idx_status (status),
    INDEX idx_floor (floor)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宿舍表';

CREATE TABLE student (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '学生记录ID',
    user_id INT UNIQUE NOT NULL COMMENT '关联user.id',
    student_no VARCHAR(20) UNIQUE NOT NULL COMMENT '学号',
    gender VARCHAR(10) COMMENT '性别',
    class_name VARCHAR(50) COMMENT '班级',
    major VARCHAR(100) COMMENT '专业',
    dormitory_id INT COMMENT '当前宿舍ID',
    check_in_date DATE COMMENT '最近入住日期',
    status VARCHAR(20) DEFAULT 'OUT' COMMENT 'IN/OUT',
    emergency_contact VARCHAR(50) COMMENT '紧急联系人',
    emergency_phone VARCHAR(20) COMMENT '紧急联系电话',
    create_time DATETIME DEFAULT NOW(),
    update_time DATETIME DEFAULT NOW() ON UPDATE NOW(),
    FOREIGN KEY (user_id) REFERENCES `user`(id) ON DELETE CASCADE,
    FOREIGN KEY (dormitory_id) REFERENCES dormitory(id) ON DELETE SET NULL,
    INDEX idx_student_no (student_no),
    INDEX idx_dormitory (dormitory_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生信息表';

CREATE TABLE check_record (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '流水ID',
    student_id INT NOT NULL COMMENT '学生ID',
    from_dormitory_id INT COMMENT '原宿舍ID',
    to_dormitory_id INT COMMENT '目标宿舍ID',
    type VARCHAR(20) NOT NULL COMMENT 'IN/OUT/CHANGE',
    check_date DATETIME DEFAULT NOW() COMMENT '操作时间',
    operator_id INT COMMENT '操作人ID',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT NOW(),
    FOREIGN KEY (student_id) REFERENCES student(id) ON DELETE CASCADE,
    FOREIGN KEY (from_dormitory_id) REFERENCES dormitory(id) ON DELETE SET NULL,
    FOREIGN KEY (to_dormitory_id) REFERENCES dormitory(id) ON DELETE SET NULL,
    FOREIGN KEY (operator_id) REFERENCES `user`(id) ON DELETE SET NULL,
    INDEX idx_student (student_id),
    INDEX idx_type (type),
    INDEX idx_date (check_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入住/退宿/换宿流水表';

CREATE TABLE repair (
    id INT PRIMARY KEY AUTO_INCREMENT COMMENT '报修ID',
    dormitory_id INT NOT NULL COMMENT '报修宿舍ID',
    reporter_id INT NOT NULL COMMENT '报修人ID',
    repair_type VARCHAR(50) NOT NULL COMMENT '报修类型',
    description VARCHAR(500) COMMENT '问题描述',
    report_time DATETIME DEFAULT NOW() COMMENT '报修时间',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT 'PENDING/DOING/DONE',
    handler_id INT COMMENT '处理人ID',
    handle_time DATETIME COMMENT '处理完成时间',
    handle_remark VARCHAR(500) COMMENT '处理备注',
    create_time DATETIME DEFAULT NOW(),
    update_time DATETIME DEFAULT NOW() ON UPDATE NOW(),
    FOREIGN KEY (dormitory_id) REFERENCES dormitory(id) ON DELETE CASCADE,
    FOREIGN KEY (reporter_id) REFERENCES student(id) ON DELETE CASCADE,
    FOREIGN KEY (handler_id) REFERENCES `user`(id) ON DELETE SET NULL,
    INDEX idx_dormitory (dormitory_id),
    INDEX idx_reporter (reporter_id),
    INDEX idx_status (status),
    INDEX idx_report_time (report_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='报修表';

INSERT INTO `user` (username, password, salt, real_name, phone, role, status) VALUES
('admin', MD5('admin123init2026'), 'init2026', '系统管理员', '13800000000', 'ADMIN', 1),
('zhanglou', MD5('admin123init2026'), 'init2026', '张楼管', '13800138011', 'BUILDING_ADMIN', 1),
('wanglou', MD5('admin123init2026'), 'init2026', '王楼管', '13800138012', 'BUILDING_ADMIN', 1),
('lisi', MD5('admin123init2026'), 'init2026', '李四', '13800138001', 'STUDENT', 1),
('wangwu', MD5('admin123init2026'), 'init2026', '王五', '13800138002', 'STUDENT', 1),
('zhaoliu', MD5('admin123init2026'), 'init2026', '赵六', '13800138003', 'STUDENT', 1),
('sunqi', MD5('admin123init2026'), 'init2026', '孙七', '13800138004', 'STUDENT', 1);

INSERT INTO building (building_name, floor_count, address, building_admin_id, status) VALUES
('A栋', 6, '校区南侧', 2, 1),
('B栋', 6, '校区北侧', 3, 1),
('致远楼', 8, '东门生活区', 2, 1);

INSERT INTO dormitory (building_id, room_number, floor, capacity, current_count, status) VALUES
(1, '301', 3, 4, 2, 'AVAILABLE'),
(1, '302', 3, 4, 0, 'AVAILABLE'),
(1, '303', 3, 4, 1, 'AVAILABLE'),
(1, '304', 3, 4, 0, 'REPAIR'),
(2, '301', 3, 4, 0, 'AVAILABLE'),
(2, '302', 3, 4, 0, 'AVAILABLE'),
(3, '501', 5, 6, 0, 'AVAILABLE'),
(3, '502', 5, 6, 0, 'AVAILABLE');

INSERT INTO student (user_id, student_no, gender, class_name, major, dormitory_id, check_in_date, status, emergency_contact, emergency_phone) VALUES
(4, '2024001', '男', '计算机1班', '计算机科学与技术', 1, '2026-03-01', 'IN', '李先生', '13900000001'),
(5, '2024002', '女', '计算机2班', '软件工程', 1, '2026-03-01', 'IN', '王女士', '13900000002'),
(6, '2024003', '男', '计算机1班', '计算机科学与技术', 3, '2026-03-05', 'IN', '赵先生', '13900000003'),
(7, '2024004', '女', '软件工程2班', '软件工程', NULL, NULL, 'OUT', '孙女士', '13900000004');

INSERT INTO check_record (student_id, from_dormitory_id, to_dormitory_id, type, operator_id, remark) VALUES
(1, NULL, 1, 'IN', 2, '新生入住'),
(2, NULL, 1, 'IN', 2, '新生入住'),
(3, NULL, 3, 'IN', 2, '调配入住');

INSERT INTO repair (dormitory_id, reporter_id, repair_type, description, status, handler_id, handle_remark) VALUES
(1, 1, '网络', '晚上网络不稳定，偶尔断线', 'PENDING', NULL, NULL),
(3, 3, '门锁', '宿舍门锁卡顿，需要检修', 'DOING', 2, '已联系维修师傅');
