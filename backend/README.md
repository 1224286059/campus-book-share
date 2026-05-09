# 后端说明

本目录为校园二手书籍循环共享平台后端工程，基于 Java 1.8、Spring Boot 2.7.x、MyBatis-Plus、MySQL 8 和 JWT 设计。

## 当前阶段内容

- 已完成可编译的 Spring Boot 基础工程
- 已预置统一返回结构 `Result`
- 已预置全局异常处理
- 已加入 MyBatis-Plus、Validation、Security、JWT、Lombok 依赖
- 已预置基础配置文件 `application.yml`

## 启动命令

```bash
mvn spring-boot:run
```

## 打包命令

```bash
mvn clean package
```

## 健康检查接口

```http
GET /api/health
```
