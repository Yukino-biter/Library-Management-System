# 书城管理系统（Bookshop Management System）

> **课程设计项目** — 黄淮学院 · 计算机与人工智能学院
> 基于 Spring Boot 的 B/S 架构图书借阅管理系统

---

## 一、课程设计简介

本项目为《Web 应用开发 / Java EE 框架技术》课程设计作品，旨在综合运用
**Java + Spring Boot + JPA + MySQL + Thymeleaf** 等主流技术栈，完成一个具备
完整业务闭环、贴近实际应用场景的图书借阅管理系统。

### 1.1 设计目标

- 熟练掌握 Spring Boot 全栈开发流程（控制层 / 业务层 / 持久层 / 表现层）
- 实践 JPA（Hibernate）ORM 映射、关系型数据库设计
- 实现 JWT 无状态认证 + BCrypt 密码加密的安全方案
- 掌握过滤器（Filter）在编码转换、权限控制、SQL 注入防护中的应用
- 培养工程化思维：分层架构、配置外置、Git 版本管理

### 1.2 业务场景

系统面向图书管理员与普通读者两类用户：

- **管理员**：维护图书信息（增删改查、封面上传）、查看全部借阅记录
- **普通读者**：浏览 / 检索图书、借阅图书、归还图书、查看个人借阅历史

### 1.3 适用课程

| 课程 | 章节 |
|------|------|
| Java EE 框架技术 | Spring Boot、Spring Data JPA |
| Web 应用开发 | MVC、Thymeleaf、过滤器 |
| 数据库原理 | 关系建模、SQL 脚本编写 |
| 软件工程 | 分层架构、Git 协作 |

---

## 二、技术栈

| 分层 | 技术 | 说明 |
|------|------|------|
| 后端框架 | Spring Boot 3.5.15 | 自动配置、内嵌 Tomcat |
| 持久层 | Spring Data JPA (Hibernate) | ORM 映射 |
| 数据库 | MySQL 8.0 | 关系型数据库 |
| 模板引擎 | Thymeleaf | 服务端渲染 |
| 认证 | JWT (JJWT 0.12.6) | 无状态 Token 认证 |
| 加密 | spring-security-crypto (BCrypt) | 密码哈希 |
| 安全 | 自定义 Filter | 编码过滤 + 非法字符拦截（SQL 注入 / XSS） |
| 工具 | Lombok | 实体类简化 |
| 运行环境 | JDK 17+ | LTS 版本 |

---

## 三、环境要求

- **JDK** 17 或以上
- **MySQL** 8.0 或以上
- **Maven** 3.6+（也可直接使用项目自带的 `mvnw` / `mvnw.cmd`）
- **IDE**（推荐）：IntelliJ IDEA

---

## 四、快速开始

### 4.1 克隆仓库

```bash
git clone <your-repo-url>.git
cd LbSysteam
```

### 4.2 配置环境变量

项目通过环境变量管理数据库账号、JWT 密钥等敏感配置，避免硬编码进代码库。

```bash
# 1. 复制示例文件
cp .env.example .env          # Windows: copy .env.example .env

# 2. 编辑 .env，按本地环境修改 DB_USERNAME / DB_PASSWORD 等
#    （不创建 .env 也可运行，将使用 application.properties 中的开发默认值）
```

> **说明**：`.env` 文件已被 `.gitignore` 忽略，不会随仓库提交，请勿将真实密码提交到 Git。
> 详细变量含义见 [.env.example](.env.example)。

### 4.3 初始化数据库

启动 MySQL 服务后，执行项目根目录下的 `book.sql` 脚本：

```bash
mysql -u root -p < book.sql
```

脚本会自动创建 `bookshop` 数据库、三张业务表及测试数据。

### 4.4 运行项目

```bash
# 方式一：Maven Wrapper
.\mvnw.cmd spring-boot:run        # Linux/Mac: ./mvnw spring-boot:run

# 方式二：IDE
# 直接运行 BookshopApplication.java 的 main 方法
```

启动成功后浏览器访问：<http://localhost:8080>

### 4.5 测试账号

| 账号 | 密码 | 角色 | 权限 |
|------|------|------|------|
| admin | 123456 | 管理员 | 图书管理 + 全部借阅记录 |
| zhangsan | 123456 | 普通用户 | 借阅 / 归还 / 个人记录 |
| lisi | 123456 | 普通用户 | 借阅 / 归还 / 个人记录 |

> 首次启动时 `DataInitializer` 会自动校验并修正测试账号密码。

---

## 五、功能模块

### 5.1 管理员功能

- 图书信息管理：新增 / 编辑 / 删除 / 列表查询
- 图书封面上传（支持多部分文件上传）
- 多条件检索：书名模糊查询 + 分类精确筛选
- 查看全部借阅记录（含借阅人、借阅 / 归还时间、状态）

### 5.2 普通用户功能

- 浏览图书列表、按书名 / 分类检索
- 查看图书详情
- 借阅图书（自动扣减库存，库存不足提示）
- 归还图书（自动恢复库存）
- 查看个人借阅历史

### 5.3 通用功能

- 用户注册 / 登录 / 退出
- 忘记密码重置
- JWT Token 认证（登录态保持 24 小时）
- BCrypt 密码加密存储
- 全局编码过滤（UTF-8）
- 非法字符拦截（SQL 注入 / XSS 防护）

---

## 六、项目结构

```
LbSysteam/
├── .env.example                      # 环境变量模板（请复制为 .env）
├── .gitignore                        # Git 忽略规则
├── book.sql                          # 数据库初始化脚本
├── pom.xml                           # Maven 依赖配置
├── mvnw / mvnw.cmd                   # Maven Wrapper
└── src/
    ├── main/
    │   ├── java/com/hhu/bookshop/
    │   │   ├── BookshopApplication.java      # 启动类
    │   │   ├── config/
    │   │   │   ├── DataInitializer.java      # 启动数据初始化
    │   │   │   └── WebConfig.java            # 过滤器注册 + 静态资源映射
    │   │   ├── controller/
    │   │   │   ├── BookController.java       # 图书 CRUD + 图片上传
    │   │   │   ├── BorrowController.java     # 借阅 / 归还
    │   │   │   └── UserController.java       # 登录 / 注册 / 重置密码
    │   │   ├── dao/
    │   │   │   ├── BookDao.java
    │   │   │   ├── BorrowDao.java
    │   │   │   └── UserDao.java
    │   │   ├── entity/
    │   │   │   ├── Book.java
    │   │   │   ├── BorrowRecord.java
    │   │   │   └── User.java
    │   │   ├── filter/
    │   │   │   ├── AuthFilter.java           # JWT 认证 + 非法字符拦截
    │   │   │   └── EncodingFilter.java       # UTF-8 编码过滤
    │   │   ├── service/
    │   │   │   ├── BookService.java
    │   │   │   ├── BorrowService.java
    │   │   │   └── UserService.java          # BCrypt 加密
    │   │   └── util/
    │   │       └── JwtUtil.java              # JWT 工具类
    │   └── resources/
    │       ├── application.properties        # 应用配置（支持环境变量覆盖）
    │       ├── static/
    │       │   ├── loginback.webp            # 登录注册页背景
    │       │   └── uploads/                  # 上传的图书封面（运行时生成）
    │       └── templates/
    │           ├── login.html
    │           ├── register.html
    │           ├── resetPassword.html
    │           ├── books/{list,add,edit,detail}.html
    │           └── borrow/{my,all}.html
    └── test/
        └── java/com/hhu/bookshop/
            └── BookshopApplicationTests.java
```

---

## 七、数据库设计

### 7.1 ER 关系

```
┌──────────┐       ┌────────────────┐       ┌──────────┐
│   user   │ 1   N │ borrow_record  │ N   1 │   book   │
│──────────│──────│────────────────│──────│──────────│
│ id (PK)  │      │ id (PK)        │      │ id (PK)  │
│ username │      │ user_id (FK)   │      │ book_name│
│ password │      │ book_id (FK)   │      │ author   │
│ nickname │      │ borrow_time    │      │ isbn     │
│ role     │      │ return_time    │      │ price    │
│ create_  │      │ status         │      │ category │
│   time   │      └────────────────┘      │ stock    │
└──────────┘                              │ ...      │
                                          └──────────┘
```

### 7.2 表说明

| 表名 | 说明 | 主要字段 |
|------|------|----------|
| `user` | 用户表 | id, username, password(BCrypt), nickname, role(admin/user), create_time |
| `book` | 图书表 | id, book_name, author, publisher, isbn, price, category, stock, description, image, create_time, update_time |
| `borrow_record` | 借阅记录表 | id, user_id, book_id, borrow_time, return_time, status(borrowed/returned) |

---

## 八、配置说明

### 8.1 环境变量列表

| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| `SERVER_PORT` | `8080` | 服务端口 |
| `DB_URL` | `jdbc:mysql://localhost:3306/bookshop?...` | 数据库连接 URL |
| `DB_USERNAME` | `root` | 数据库用户名 |
| `DB_PASSWORD` | `root` | 数据库密码 |
| `JWT_SECRET` | `BookshopSystem2025SecretKey...` | JWT 签名密钥（≥32 字节） |
| `JWT_EXPIRATION` | `86400000` | Token 有效期（毫秒，默认 24h） |
| `UPLOAD_PATH` | `src/main/resources/static/uploads/` | 图书封面上传目录 |

### 8.2 配置优先级

Spring Boot 配置加载优先级（从高到低）：

1. 命令行参数：`--DB_PASSWORD=yourpass`
2. JVM 系统属性：`-DDB_PASSWORD=yourpass`
3. 操作系统环境变量
4. `application.properties` 中的 `${VAR:default}` 默认值

---

## 九、安全说明

- 密码采用 **BCrypt** 加盐哈希存储，不可逆
- 登录认证使用 **JWT**，Token 在客户端 Cookie 中保存，服务端无状态
- 全局 `EncodingFilter` 统一 UTF-8 编码，避免中文乱码
- `AuthFilter` 拦截请求做 JWT 校验，并对参数做非法字符过滤，防御 SQL 注入与 XSS
- `.env` 文件被 Git 忽略，防止敏感配置泄露
- 生产环境务必修改默认的 `DB_PASSWORD` 与 `JWT_SECRET`

---

## 十、构建打包

```bash
# 打包为可执行 jar
.\mvnw.cmd clean package -DskipTests

# 运行 jar（通过环境变量覆盖配置）
java -jar target/LbSysteam-0.0.1-SNAPSHOT.jar \
     --DB_PASSWORD=your_password \
     --JWT_SECRET=your_long_random_secret
```

---

## 十一、课程设计总结

### 11.1 主要工作

1. 完成基于 Spring Boot 的三层架构（Controller / Service / DAO）设计与实现
2. 使用 JPA 完成对 MySQL 数据库的持久化操作
3. 实现 JWT + BCrypt 的安全认证方案
4. 编写自定义 Filter 完成编码统一与安全防护
5. 使用 Thymeleaf 完成服务端渲染页面
6. 完成数据库初始化脚本 `book.sql`，含表结构与测试数据

### 11.2 待改进方向

- 引入 Spring Security 替代自定义 Filter，获得更完善的安全体系
- 借阅记录增加逾期提醒与罚款规则
- 图书封面支持对象存储（如 OSS / MinIO）
- 引入 Redis 缓存热门图书，提升查询性能
- 前端引入 Vue / React，实现前后端分离
- 增加单元测试与集成测试覆盖率

---

## 十二、License

本项目仅用于课程设计与学习交流用途。
