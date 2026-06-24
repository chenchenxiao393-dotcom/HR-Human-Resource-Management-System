-- 企业人事管理系统数据库
CREATE DATABASE IF NOT EXISTS hr_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE hr_management;

-- 部门表
CREATE TABLE department (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    department_code VARCHAR(50) NOT NULL UNIQUE COMMENT '部门编码',
    department_name VARCHAR(100) NOT NULL COMMENT '部门名称',
    manager_id BIGINT COMMENT '负责人ID',
    description VARCHAR(500) COMMENT '部门描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_department_code (department_code),
    INDEX idx_department_name (department_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='部门表';

-- 员工表
CREATE TABLE employee (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_code VARCHAR(50) NOT NULL UNIQUE COMMENT '员工编号',
    name VARCHAR(100) NOT NULL COMMENT '姓名',
    gender VARCHAR(10) COMMENT '性别',
    birth_date DATE COMMENT '出生日期',
    phone VARCHAR(20) COMMENT '电话',
    email VARCHAR(100) COMMENT '邮箱',
    address VARCHAR(255) COMMENT '地址',
    household_type VARCHAR(50) COMMENT '户口类型',
    political_affiliation VARCHAR(50) COMMENT '政治面貌',
    health_status VARCHAR(50) COMMENT '健康状况',
    hire_date DATE COMMENT '入职日期',
    department_id BIGINT COMMENT '所属部门',
    position VARCHAR(100) COMMENT '职位',
    employee_status VARCHAR(20) DEFAULT '在职' COMMENT '员工状态',
    salary DECIMAL(10,2) COMMENT '薪资',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_employee_code (employee_code),
    INDEX idx_name (name),
    INDEX idx_department_id (department_id),
    INDEX idx_employee_status (employee_status),
    FOREIGN KEY (department_id) REFERENCES department(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工表';

-- 合同表
CREATE TABLE contract (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    contract_code VARCHAR(50) NOT NULL UNIQUE COMMENT '合同编号',
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    contract_type VARCHAR(50) COMMENT '合同类型',
    sign_date DATE COMMENT '签订日期',
    start_date DATE COMMENT '开始日期',
    end_date DATE COMMENT '到期日期',
    status VARCHAR(20) DEFAULT '生效中' COMMENT '状态',
    content TEXT COMMENT '合同内容',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_employee_id (employee_id),
    INDEX idx_status (status),
    INDEX idx_end_date (end_date),
    FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='合同表';

-- 考勤表
CREATE TABLE attendance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    attendance_date DATE NOT NULL COMMENT '考勤日期',
    check_in_time VARCHAR(20) COMMENT '上班打卡时间',
    check_out_time VARCHAR(20) COMMENT '下班打卡时间',
    status VARCHAR(20) DEFAULT '正常' COMMENT '状态',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_employee_date (employee_id, attendance_date),
    INDEX idx_attendance_date (attendance_date),
    INDEX idx_status (status),
    FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考勤表';

-- 加班表
CREATE TABLE overtime (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    overtime_date DATE NOT NULL COMMENT '加班日期',
    start_time VARCHAR(20) COMMENT '开始时间',
    end_time VARCHAR(20) COMMENT '结束时间',
    hours DECIMAL(5,2) COMMENT '加班时长',
    reason VARCHAR(500) COMMENT '加班原因',
    status VARCHAR(20) DEFAULT '待审批' COMMENT '状态',
    approve_remark VARCHAR(255) COMMENT '审批备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_employee_id (employee_id),
    INDEX idx_overtime_date (overtime_date),
    INDEX idx_status (status),
    FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='加班表';

-- 出差表
CREATE TABLE business_trip (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    origin VARCHAR(100) COMMENT '出发地',
    destination VARCHAR(100) COMMENT '目的地',
    start_date DATE NOT NULL COMMENT '开始日期',
    end_date DATE NOT NULL COMMENT '结束日期',
    reason VARCHAR(500) COMMENT '出差原因',
    status VARCHAR(20) DEFAULT '待审批' COMMENT '状态',
    approve_remark VARCHAR(255) COMMENT '审批备注',
    allowance DECIMAL(10,2) COMMENT '出差补贴',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_employee_id (employee_id),
    INDEX idx_dates (start_date, end_date),
    INDEX idx_status (status),
    FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出差表';

-- 人事变动表
CREATE TABLE personnel_change (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    change_type VARCHAR(50) NOT NULL COMMENT '变动类型',
    original_value VARCHAR(255) COMMENT '原值',
    new_value VARCHAR(255) COMMENT '新值',
    change_date DATE NOT NULL COMMENT '变动日期',
    reason VARCHAR(500) COMMENT '变动原因',
    approve_by VARCHAR(100) COMMENT '审批人',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_employee_id (employee_id),
    INDEX idx_change_type (change_type),
    INDEX idx_change_date (change_date),
    FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='人事变动表';

-- 离职表
CREATE TABLE resignation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    resignation_date DATE NOT NULL COMMENT '离职日期',
    resignation_type VARCHAR(50) COMMENT '离职类型',
    reason VARCHAR(500) COMMENT '离职原因',
    handover_person VARCHAR(100) COMMENT '交接人',
    remark VARCHAR(500) COMMENT '备注',
    approve_status VARCHAR(20) DEFAULT '待审批' COMMENT '审批状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_employee_id (employee_id),
    INDEX idx_resignation_date (resignation_date),
    INDEX idx_approve_status (approve_status),
    FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='离职表';

-- 培训表
CREATE TABLE training (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    training_code VARCHAR(50) NOT NULL UNIQUE COMMENT '培训编号',
    training_name VARCHAR(200) NOT NULL COMMENT '培训名称',
    training_type VARCHAR(50) COMMENT '培训类型',
    training_date DATE COMMENT '培训日期',
    duration INT COMMENT '时长(小时)',
    lecturer VARCHAR(100) COMMENT '讲师',
    location VARCHAR(100) COMMENT '培训地点',
    content TEXT COMMENT '培训内容',
    max_participants INT COMMENT '最大参与人数',
    status VARCHAR(20) DEFAULT '计划中' COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_training_code (training_code),
    INDEX idx_training_name (training_name),
    INDEX idx_training_date (training_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训表';

-- 培训记录表
CREATE TABLE training_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    training_id BIGINT NOT NULL COMMENT '培训ID',
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    status VARCHAR(20) DEFAULT '已报名' COMMENT '状态',
    attendance_status VARCHAR(20) COMMENT '出勤状态',
    score DECIMAL(5,2) COMMENT '成绩',
    evaluation VARCHAR(500) COMMENT '评价',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_training_id (training_id),
    INDEX idx_employee_id (employee_id),
    FOREIGN KEY (training_id) REFERENCES training(id) ON DELETE CASCADE,
    FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='培训记录表';

-- 考核表
CREATE TABLE appraisal (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    appraisal_date DATE NOT NULL COMMENT '考核日期',
    appraisal_period VARCHAR(50) COMMENT '考核周期',
    appraisal_type VARCHAR(50) COMMENT '考核类型',
    score DECIMAL(5,2) COMMENT '得分',
    grade VARCHAR(10) COMMENT '等级',
    evaluator VARCHAR(100) COMMENT '评价人',
    evaluation TEXT COMMENT '评价内容',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_employee_id (employee_id),
    INDEX idx_appraisal_date (appraisal_date),
    INDEX idx_appraisal_period (appraisal_period),
    FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考核表';

-- 奖惩表
CREATE TABLE reward_punishment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL COMMENT '员工ID',
    type VARCHAR(20) NOT NULL COMMENT '类型',
    reason VARCHAR(500) COMMENT '原因',
    amount DECIMAL(10,2) COMMENT '金额',
    rp_date DATE NOT NULL COMMENT '奖惩日期',
    status VARCHAR(20) DEFAULT '待生效' COMMENT '状态',
    approve_by VARCHAR(100) COMMENT '审批人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_employee_id (employee_id),
    INDEX idx_type (type),
    INDEX idx_rp_date (rp_date),
    INDEX idx_status (status),
    FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='奖惩表';

-- 角色表
CREATE TABLE role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色编码',
    role_name VARCHAR(100) NOT NULL COMMENT '角色名称',
    permissions VARCHAR(1000) COMMENT '权限列表',
    description VARCHAR(500) COMMENT '角色描述',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_role_code (role_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表';

-- 用户表
CREATE TABLE `user` (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    real_name VARCHAR(100) COMMENT '真实姓名',
    role VARCHAR(50) NOT NULL COMMENT '角色',
    employee_id BIGINT COMMENT '关联员工ID',
    status VARCHAR(20) DEFAULT 'NORMAL' COMMENT '状态',
    need_change_password TINYINT DEFAULT 0 COMMENT '是否需要修改密码',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_username (username),
    INDEX idx_role (role),
    FOREIGN KEY (employee_id) REFERENCES employee(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 备份记录表
CREATE TABLE backup_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    backup_type VARCHAR(20) COMMENT '备份类型',
    file_name VARCHAR(255) COMMENT '文件名',
    file_path VARCHAR(500) COMMENT '文件路径',
    file_size BIGINT COMMENT '文件大小',
    status VARCHAR(20) COMMENT '状态',
    operator VARCHAR(100) COMMENT '操作人',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_backup_type (backup_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='备份记录表';

-- 操作日志表
CREATE TABLE operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) COMMENT '用户名',
    operation VARCHAR(100) COMMENT '操作',
    method VARCHAR(255) COMMENT '方法',
    ip VARCHAR(50) COMMENT 'IP地址',
    location VARCHAR(255) COMMENT '操作地点',
    params TEXT COMMENT '请求参数',
    result TEXT COMMENT '返回结果',
    status INT DEFAULT 1 COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- 任务表
CREATE TABLE IF NOT EXISTS task (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_name VARCHAR(200) NOT NULL COMMENT '任务名称',
    description TEXT COMMENT '任务描述',
    employee_id BIGINT COMMENT '分配员工ID',
    employee_name VARCHAR(100) COMMENT '员工姓名',
    deadline DATE COMMENT '截止日期',
    status VARCHAR(20) DEFAULT '未开始' COMMENT '任务状态',
    progress INT DEFAULT 0 COMMENT '进度',
    priority VARCHAR(20) DEFAULT '普通' COMMENT '优先级',
    creator VARCHAR(100) COMMENT '创建人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_employee_id (employee_id),
    INDEX idx_status (status),
    INDEX idx_deadline (deadline)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务表';

-- 通知表
CREATE TABLE IF NOT EXISTS notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL COMMENT '接收员工ID',
    title VARCHAR(200) NOT NULL COMMENT '通知标题',
    content TEXT COMMENT '通知内容',
    type VARCHAR(50) DEFAULT '系统通知' COMMENT '通知类型',
    status VARCHAR(20) DEFAULT '未读' COMMENT '状态',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    INDEX idx_employee_id (employee_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

-- 初始化默认管理员账号 (密码: 123456)
INSERT INTO `user` (username, password, real_name, role, status) VALUES
('admin', '$2a$10$K4I.gv.FJKfYSIAuuQrVGuveHFFvDgr8eK5pzdrktqv.vWPFMzAue', '系统管理员', 'ADMIN', 'NORMAL');

-- 初始化默认角色
INSERT INTO role (role_code, role_name, permissions, description) VALUES
('ADMIN', '系统管理员', '*:*:*', '拥有系统所有权限'),
('HR', '人事专员', 'employee:*:*,department:*:*,contract:*:*,attendance:*:*,overtime:*:*,businessTrip:*:*,personnelChange:*:*,resignation:*:*,training:*:*,trainingRecord:*:*,appraisal:*:*,rewardPunishment:*:*,user:read', '人事管理权限'),
('EMPLOYEE', '普通员工', 'employee:read,attendance:*:*,overtime:read,training:read,trainingRecord:read,appraisal:read,rewardPunishment:read', '员工基本权限');

-- 初始化默认部门
INSERT INTO department (department_code, department_name, description) VALUES
('DEPT001', '总经理办公室', '公司最高管理层'),
('DEPT002', '人力资源部', '负责公司人力资源管理'),
('DEPT003', '财务部', '负责公司财务管理'),
('DEPT004', '技术部', '负责公司技术研发'),
('DEPT005', '市场部', '负责公司市场营销');
