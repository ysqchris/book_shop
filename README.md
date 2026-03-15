# book_shop - 二手书买卖平台

一个基于Spring Boot + Vue.js的二手书在线交易平台，提供用户购买前端和管理后台。

## 项目介绍

本项目是一个完整的二手书交易平台，包含：
- **用户前端**：图书浏览、搜索、购买、订单管理等功能
- **管理后台**：图书管理、用户管理、订单管理等功能
- **后端API**：基于Spring Boot的RESTful API服务

## 技术栈

### 后端技术
- **框架**：Spring Boot 3.x
- **ORM**：MyBatis-Plus
- **数据库**：MySQL 8.0
- **安全**：Spring Security + JWT
- **缓存**：Redis
- **构建工具**：Maven

### 前端技术
- **用户前端**：Vue 3 + Vite + Element Plus
- **管理后台**：Vue 3 + Vite + Element Plus
- **状态管理**：Pinia
- **路由**：Vue Router
- **HTTP客户端**：Axios

## 项目结构

```
book_shop/
├── backend/                 # Spring Boot后端项目
│   ├── src/main/java/      # Java源代码
│   ├── src/main/resources/ # 配置文件
│   └── pom.xml             # Maven配置
├── frontend-user/          # 用户前端项目
│   ├── src/                # Vue源代码
│   ├── public/             # 静态资源
│   └── package.json        # 依赖配置
├── frontend-admin/         # 管理后台项目（待开发）
├── docs/                   # 文档
│   └── database.sql        # 数据库脚本
└── README.md              # 项目说明
```

## 快速开始

### 环境要求

- JDK 17+
- Node.js 16+
- MySQL 8.0+
- Redis 6.0+

### 数据库初始化

1. 创建数据库：
```sql
CREATE DATABASE book_shop CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行初始化脚本：
```sql
source docs/database.sql
```

### 后端启动

1. 进入backend目录：
```bash
cd backend
```

2. 修改数据库配置（`src/main/resources/application.yml`）：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/book_shop
    username: your_username
    password: your_password
```

3. 启动应用：
```bash
mvn spring-boot:run
```

### 前端启动

1. 进入frontend-user目录：
```bash
cd frontend-user
```

2. 安装依赖：
```bash
npm install
```

3. 启动开发服务器：
```bash
npm run dev
```

4. 访问地址：http://localhost:3000

## 功能模块

### 用户前端功能
- [x] 用户注册/登录
- [x] 图书浏览和搜索
- [x] 图书详情查看
- [ ] 购物车管理
- [ ] 订单管理
- [ ] 个人中心

### 管理后台功能（待开发）
- [ ] 图书管理
- [ ] 用户管理
- [ ] 订单管理
- [ ] 数据统计

### 后端API功能
- [x] 用户管理API
- [x] 图书管理API
- [x] 分类管理API
- [ ] 订单管理API
- [ ] 购物车API

## API接口文档

### 用户相关接口

#### 用户注册
```
POST /api/user/register
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123",
  "email": "test@example.com"
}
```

#### 用户登录
```
POST /api/user/login
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}
```

#### 获取用户信息
```
GET /api/user/info/{userId}
Authorization: Bearer {token}
```

### 图书相关接口

#### 获取图书列表
```
GET /api/books
参数：
- page: 页码（默认1）
- size: 每页数量（默认12）
- categoryId: 分类ID
- keyword: 搜索关键词
- sort: 排序方式（newest, price_asc, price_desc）
```

#### 获取图书详情
```
GET /api/book/{id}
```

## 部署说明

### 后端部署

1. 打包应用：
```bash
mvn clean package
```

2. 运行jar包：
```bash
java -jar target/book_shop-1.0.0.jar
```

### 前端部署

1. 构建生产版本：
```bash
npm run build
```

2. 部署dist目录到Web服务器

## 开发计划

- [ ] 完善购物车功能
- [ ] 实现订单流程
- [ ] 开发管理后台
- [ ] 添加支付集成
- [ ] 实现图片上传
- [ ] 优化搜索功能

## 贡献指南

1. Fork 本项目
2. 创建特性分支
3. 提交更改
4. 推送到分支
5. 创建Pull Request

## 许可证

MIT License

## 联系方式

如有问题或建议，请通过以下方式联系：
- 邮箱：developer@example.com
- Issues：项目Issues页面