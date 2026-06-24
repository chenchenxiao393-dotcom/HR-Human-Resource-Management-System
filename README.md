# 企业人事管理系统

一个基于前后端分离架构的企业人事管理系统，提供员工管理、部门管理、合同管理、考勤管理、加班出差、培训考核、薪资管理等完整的人力资源管理功能。

## 技术栈

### 前端
- **框架：** Vue 3 (Composition API + script setup)
- **UI组件库：** Element Plus
- **状态管理：** Pinia
- **路由：** Vue Router 4.x
- **构建工具：** Vite
- **HTTP请求：** Axios
- **日期处理：** Day.js

### 后端
- **框架：** Spring Boot 2.7.18
- **ORM框架：** MyBatis Plus 3.5.3.1
- **安全框架：** Spring Security + JWT
- **数据库：** MySQL
- **连接池：** HikariCP（Spring Boot默认）
- **构建工具：** Maven

## 系统功能

### 管理员功能
- **工作台：** 数据统计概览、待审批事项、人事变动、合同到期提醒、快捷操作
- **人员管理：** 员工信息增删改查、自动创建用户账号
- **部门管理：** 部门信息维护
- **合同管理：** 员工合同管理、到期提醒
- **考勤管理：** 考勤记录查看、统计、导出
- **加班管理：** 加班申请审批
- **出差管理：** 出差申请审批
- **人事变动：** 员工调动、晋升等记录管理
- **离职管理：** 员工离职申请与审批
- **培训管理：** 培训计划、培训记录管理
- **考核管理：** 员工绩效考核
- **奖惩管理：** 奖励与惩罚记录
- **系统维护：** 数据备份与恢复
- **任务管理：** 工作任务分配与跟踪
- **通知管理：** 系统通知发布

### 员工功能
- **工作台：** 个人数据概览、今日考勤、快捷签到签退
- **员工信息：** 查看和编辑个人信息
- **部门结构：** 查看组织架构
- **合同信息：** 查看个人合同
- **考勤记录：** 个人考勤记录查询、签到签退
- **加班申请：** 提交加班申请
- **出差申请：** 提交出差申请
- **人事变动：** 查看个人变动记录
- **离职管理：** 提交离职申请
- **培训管理：** 培训报名、查看培训记录
- **奖惩记录：** 查看个人奖惩记录
- **工资查询：** 薪资查询
- **任务管理：** 查看个人任务
- **修改密码：** 修改登录密码

## 项目结构

```
企业人事管理/
├── backend/                    # 后端项目
│   ├── src/main/java/com/hr/
│   │   ├── common/             # 公共类
│   │   ├── config/             # 配置类
│   │   ├── controller/         # 控制器（23个）
│   │   ├── entity/             # 实体类（20个）
│   │   ├── mapper/             # 数据访问层
│   │   ├── schedule/           # 定时任务
│   │   ├── security/           # 安全相关
│   │   └── service/            # 服务层
│   ├── src/main/resources/
│   │   └── application.yml     # 应用配置
│   └── pom.xml
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── api/                # API接口（21个）
│   │   ├── assets/             # 静态资源
│   │   ├── router/             # 路由配置
│   │   ├── stores/             # Pinia状态管理
│   │   ├── utils/              # 工具函数
│   │   └── views/              # 页面组件
│   ├── index.html
│   ├── vite.config.js
│   └── package.json
├── database/
│   └── schema.sql              # 数据库脚本
├── 测试报告.md                  # 项目测试报告
└── README.md                   # 项目说明文档
```

## 快速开始

### 环境要求

- JDK 8+
- Node.js 14+
- MySQL 5.7+
- Maven 3.6+

### 数据库配置

1. 创建数据库：
```sql
CREATE DATABASE IF NOT EXISTS hr_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行数据库脚本 `database/schema.sql` 初始化表结构和初始数据

3. 默认管理员账号：
   - 用户名：`admin`
   - 密码：`123456`

### 后端启动

1. 修改数据库配置 `backend/src/main/resources/application.yml`：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/hr_management?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

2. 启动后端服务：
```bash
cd backend
mvn spring-boot:run
```

后端服务运行在 `http://localhost:8080`

### 前端启动

1. 安装依赖：
```bash
cd frontend
npm install
```

2. 启动开发服务器：
```bash
npm run dev
```

前端服务运行在 `http://localhost:5173`

## 系统角色

| 角色 | 编码 | 说明 |
|------|------|------|
| 系统管理员 | ADMIN | 拥有系统所有权限 |
| 人事专员 | HR | 负责人力资源管理相关工作 |
| 普通员工 | EMPLOYEE | 基本员工权限 |

## 数据库表结构

| 表名 | 说明 |
|------|------|
| department | 部门表 |
| employee | 员工表 |
| contract | 合同表 |
| attendance | 考勤表 |
| overtime | 加班表 |
| business_trip | 出差表 |
| personnel_change | 人事变动表 |
| resignation | 离职表 |
| training | 培训表 |
| training_record | 培训记录表 |
| appraisal | 考核表 |
| reward_punishment | 奖惩表 |
| role | 角色表 |
| user | 用户表 |
| backup_record | 备份记录表 |
| operation_log | 操作日志表 |
| task | 任务表 |
| notification | 通知表 |

## 主要接口

### 认证接口
- `POST /api/auth/login` - 登录
- `POST /api/auth/register` - 注册
- `GET /api/auth/current` - 获取当前用户
- `PUT /api/auth/change-password` - 修改密码

### 员工管理
- `GET /api/employees` - 员工列表（分页+筛选）
- `POST /api/employees` - 新增员工
- `PUT /api/employees/{id}` - 更新员工
- `DELETE /api/employees/{id}` - 删除员工

### 考勤管理
- `GET /api/attendance` - 考勤列表
- `POST /api/attendance/check-in` - 签到
- `POST /api/attendance/check-out` - 签退
- `GET /api/attendance/statistics` - 考勤统计
- `GET /api/attendance/my` - 我的考勤

### 合同管理
- `GET /api/contracts` - 合同列表
- `GET /api/contracts/my` - 我的合同
- `GET /api/contracts/expiring` - 即将到期合同

## 定时任务

- **凌晨自动处理未签退考勤**：每天00:00执行，将前一天只签到未签退的记录状态设置为"未签退"

## 部署说明

### 前端打包
```bash
cd frontend
npm run build
```
打包产物在 `frontend/dist` 目录

### 后端打包
```bash
cd backend
mvn clean package
```
打包产物在 `backend/target/hr-management-1.0.0.jar`

### 生产部署
1. 将前端 `dist` 目录的静态文件部署到 Nginx
2. 后端使用 `java -jar` 命令运行
3. 配置 Nginx 反向代理到后端 API

## 特色功能

- ✅ 前后端分离架构
- ✅ JWT 身份认证
- ✅ 基于角色的权限控制（RBAC）
- ✅ 响应式布局设计
- ✅ 考勤自动处理（定时任务）
- ✅ 合同到期提醒
- ✅ 审批工作流
- ✅ 数据备份与恢复
- ✅ 操作日志记录

## 注意事项

1. 首次登录后建议修改默认密码
2. 请确保 MySQL 服务已启动
3. 生产环境请修改默认的 JWT 密钥
4. 建议定期备份数据库

## 许可证

本项目仅供学习和研究使用。

---

*最后更新：2026-06-24*
