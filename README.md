# 校园二手书籍循环共享平台

“校园二手书籍循环共享平台设计与实现”是一个面向高校场景的本科毕业设计项目。系统重点不是普通二手买卖，而是突出教材与资料在校园中的持续流转，围绕出售、借阅、交换、捐赠、再次共享、流转记录、积分与评价、管理员审核等完整业务链路展开设计。

## 项目简介

平台面向在校学生和管理员两类角色：

- 普通用户可以发布书籍、申请购买、申请借阅、申请交换、申请领取捐赠书籍。
- 书籍在完成一次共享后，可以由当前持有人再次共享，形成“循环共享”闭环。
- 管理员负责书籍审核、分类管理、用户管理、评价管理、举报处理和订单查看。

## 系统特色

- 支持出售、借阅、交换、捐赠四种共享方式。
- 使用 `circulation_record` 记录书籍每一次关键流转行为。
- 使用 `circulation_count` 统计书籍流转次数。
- 使用 `owner_id` 表示当前持有人。
- 支持“再次共享”功能，突出循环共享主题。
- 支持书籍位置展示，便于线下查看存放地点并完成交付。
- 自动生成积分记录 `points_record`。
- 支持订单完成后的评价与举报。
- 支持管理员审核机制，保证书籍上架质量。

## 技术栈

### 后端

- Java 1.8 / JDK 8
- Spring Boot 2.7.18
- Maven
- MyBatis-Plus 3.5.5
- MySQL 8.x
- Lombok
- JWT
- Spring Validation
- 全局异常处理
- 统一返回结果 `Result`

### 前端

- Vue 3
- Vite
- Element Plus
- Axios
- Vue Router
- Pinia

### 数据库

- MySQL 8.x

## 项目目录结构

```text
campus-book-share/
├── backend/                Spring Boot 后端
├── frontend/               Vue 3 前端
├── sql/                    数据库脚本与初始化数据
├── docs/                   接口文档、截图指南、演示账号说明
├── docker-compose.yml      MySQL 容器编排
└── README.md
```

## 数据库说明

### 默认配置

后端默认数据库配置位于 [backend/src/main/resources/application.yml](/e:/campus-book-share/backend/src/main/resources/application.yml)：

- 主机：`localhost`
- 端口：`3306`
- 数据库名：`campus_book_share`
- 用户名：`root`
- 密码：`123456`

### 端口与密码说明

- 仓库默认配置仍以本地 MySQL `3306` 为准。
- 如果本地 `3306` 已被占用，或者 `root` 密码不是 `123456`，请修改 `application.yml` 中的：
  - `spring.datasource.url`
  - `spring.datasource.username`
  - `spring.datasource.password`
- 联调阶段曾使用过本机临时 `3307` 测试库做验证，但该配置没有写死进仓库默认配置。
- 如果你也想切换到其他端口，例如 `3307`，只需修改 `jdbc:mysql://localhost:3306/...` 中的端口即可。

## Docker 启动 MySQL 方式

[docker-compose.yml](/e:/campus-book-share/docker-compose.yml) 默认配置如下：

- 容器名：`campus-book-share-mysql`
- 镜像：`mysql:8.0`
- 数据库名：`campus_book_share`
- 用户名：`root`
- 密码：`123456`
- 宿主机端口：`3306`

启动命令：

```bash
docker-compose up -d
```

说明：

- 首次启动时会自动挂载并执行 `sql/schema.sql` 和 `sql/data.sql`
- 初始化脚本已包含 `user.address` 与 `book.book_location` 字段及示例数据
- SQL 目录会挂载到容器的 `/docker-entrypoint-initdb.d`

## 本地 MySQL 初始化方式

如果不使用 Docker，而是使用本地 MySQL，请手动执行以下脚本：

1. `sql/schema.sql`
2. `sql/data.sql`

推荐顺序：

```bash
mysql -uroot -p
CREATE DATABASE campus_book_share DEFAULT CHARACTER SET utf8mb4;
USE campus_book_share;
SOURCE sql/schema.sql;
SOURCE sql/data.sql;
```

说明：

- 请先确保数据库已创建。
- `sql/schema.sql` 已同步新增用户常用联系地址和书籍位置字段，`sql/data.sql` 提供了示例地址数据。
- 如果使用的是 Navicat、DataGrip 或 MySQL Workbench，也可以直接执行这两个 SQL 文件。

### 已运行旧版本数据库的增量更新

如果数据库已经按旧版本初始化过，不要删库重建，直接执行：

1. `sql/update_add_location_fields.sql`

这个脚本用于给旧数据库补充：

- `user.address`
- `book.book_location`

Docker MySQL 执行方式：

```powershell
Get-Content .\sql\update_add_location_fields.sql | docker exec -i campus-book-share-mysql mysql -uroot -p123456 campus_book_share
```

如果当前终端支持输入重定向，也可以使用：

```bash
docker exec -i campus-book-share-mysql mysql -uroot -p123456 campus_book_share < sql/update_add_location_fields.sql
```

## 后端启动方式

### 直接运行

```bash
cd backend
mvn spring-boot:run
```

### 打包运行

```bash
cd backend
mvn clean package
java -jar target/campus-book-share-backend-0.0.1-SNAPSHOT.jar
```

### Java 版本说明

- 本项目后端固定使用 Java 1.8 / JDK 8
- Spring Boot 固定使用 2.7.x
- `pom.xml` 已显式配置：
  - `java.version=1.8`
  - `maven-compiler-plugin source=1.8`
  - `maven-compiler-plugin target=1.8`

## 前端启动方式

```bash
cd frontend
npm install
npm run dev
```

生产构建命令：

```bash
cd frontend
npm run build
```

## 测试账号

- 管理员：`admin / 123456`
- 普通用户：`zhangsan / 123456`
- 普通用户：`lisi / 123456`
- 普通用户：`wangwu / 123456`

更多说明见 [docs/DEMO_ACCOUNTS.md](/e:/campus-book-share/docs/DEMO_ACCOUNTS.md)。

## 核心功能清单

- 用户注册、登录、JWT 鉴权
- 分类管理
- 书籍发布与审核
- 书籍首页查询、筛选、详情展示
- 出售共享
- 借阅共享
- 交换共享
- 捐赠共享
- 再次共享
- 书籍位置展示
- 订单流转
- 借阅记录
- 流转记录
- 积分与积分记录
- 评价与举报
- 管理员后台管理

## 完整演示流程

以下流程已经写入系统设计与联调验证，适合答辩时演示：

1. `admin / 123456` 登录后台。
2. `zhangsan / 123456` 登录前台。
3. `zhangsan` 发布一本书，选择“捐赠”。
4. 书籍状态显示为“待审核”。
5. `admin` 进入后台“书籍审核”页面。
6. `admin` 审核通过该书。
7. `lisi / 123456` 登录前台。
8. `lisi` 在首页看到该捐赠书籍。
9. `lisi` 进入详情页，先查看书籍位置后点击“申请领取”。
10. `zhangsan` 进入“我的订单”，看到收到的申请。
11. `zhangsan` 点击同意申请。
12. `lisi` 进入“我的订单”，点击确认完成。
13. 订单状态变为“已完成”。
14. 数据库生成 `circulation_record`。
15. `book.owner_id` 更新为 `lisi`。
16. `book.circulation_count` 增加。
17. `points_record` 中生成积分记录。
18. `lisi` 进入“我的持有书籍”页面。
19. `lisi` 点击“再次共享”。
20. `lisi` 重新选择共享方式为“借阅”，并可重新填写新的书籍位置。
21. 书籍重新进入“待审核”。
22. `admin` 再次审核通过。
23. 首页重新显示该书，且共享方式变为“借阅”。

## 常见问题

### 1. 后端启动时报数据库连接失败

请检查：

- MySQL 是否启动
- 端口是否为 `3306`
- 用户名密码是否和 `application.yml` 一致
- 是否已执行 `sql/schema.sql` 和 `sql/data.sql`

### 2. 本地 3306 被占用怎么办

可以改用其他端口，例如 `3307`，并同步修改 `application.yml` 的数据库 URL。

### 3. Docker 启动后没有初始化数据

请确认：

- 是第一次创建容器
- `sql/` 目录已正确挂载
- 没有复用旧的 MySQL 数据卷

必要时删除旧容器和数据卷后重新执行 `docker-compose up -d`。

### 4. 前端登录后提示无权限

请确认当前账号角色：

- `admin` 用于后台管理
- `zhangsan / lisi / wangwu` 用于普通用户前台流程

### 5. 页面能打开但没有数据

请先检查后端接口是否启动正常，再检查数据库是否已导入初始化数据。

## 毕业论文截图建议

- 第 5 章建议先放系统首页、书籍详情、发布页面、订单页面、再次共享页面。
- 后续展示管理员后台时，优先使用“书籍审核页面”和“举报管理页面”。
- 数据库截图建议补充：
  - `book`
  - `book_order`
  - `borrow_record`
  - `circulation_record`
  - `points_record`
- 更细的截图清单见 [docs/SCREENSHOT_GUIDE.md](/e:/campus-book-share/docs/SCREENSHOT_GUIDE.md)。
