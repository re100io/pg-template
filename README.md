# PostgreSQL Template Backend

基于 JDK 21、Spring Boot 和 PostgreSQL 的后端模板项目。

## 技术栈

- **JDK**: 21 (LTS)
- **Spring Boot**: 3.4.1
- **Kotlin**: 2.1.0
- **数据库**: PostgreSQL
- **构建工具**: Maven

## 项目结构

```
src/
├── main/
│   ├── kotlin/
│   │   └── com/re100io/
│   │       ├── config/          # 配置类
│   │       ├── controller/      # REST 控制器
│   │       ├── dto/            # 数据传输对象
│   │       ├── entity/         # JPA 实体
│   │       ├── exception/      # 异常处理
│   │       ├── repository/     # 数据访问层
│   │       ├── service/        # 业务逻辑层
│   │       └── Main.kt         # 应用入口
│   └── resources/
│       ├── application.properties
│       ├── application-dev.properties
│       └── application-prod.properties
└── test/
    └── kotlin/
        └── com/re100io/
            └── service/        # 服务层测试
```

## 快速开始

### 1. 环境要求

- JDK 21 (LTS)
- PostgreSQL 12+
- Maven 3.6+

### 2. 数据库设置

创建数据库：
```sql
CREATE DATABASE pg_template;
CREATE DATABASE pg_template_dev;
```

### 3. 快速启动

#### 方式一：使用启动脚本（推荐）
```bash
./start.sh
```

#### 方式二：手动启动

1. **启动数据库**（使用 Docker）：
```bash
docker-compose up postgres -d
```

2. **构建并运行应用**：
```bash
mvn clean package -DskipTests
java -jar target/pg-template-1.0-SNAPSHOT.jar --spring.profiles.active=dev
```

生产环境：
```bash
java -jar target/pg-template-1.0-SNAPSHOT.jar --spring.profiles.active=prod
```

### 4. API 端点

基础路径: `http://localhost:8080/api`

#### 用户管理 API

- `POST /users` - 创建用户
- `GET /users/{id}` - 获取用户详情
- `GET /users` - 获取用户列表
- `PUT /users/{id}` - 更新用户
- `DELETE /users/{id}` - 删除用户
- `GET /users/search?keyword=xxx` - 搜索用户

#### 示例请求

创建用户：
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "email": "test@example.com",
    "fullName": "Test User"
  }'
```

## 配置说明

### 数据库配置

在 `application.properties` 中配置数据库连接：

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/pg_template
spring.datasource.username=postgres
spring.datasource.password=password
```

### 环境配置

- `application-dev.properties`: 开发环境配置
- `application-prod.properties`: 生产环境配置

## 测试

运行测试：
```bash
mvn test
```

## 构建

构建项目：
```bash
mvn clean package
```

生成的 JAR 文件位于 `target/` 目录下。

## 部署

使用生成的 JAR 文件部署：
```bash
java -jar target/pg-template-1.0-SNAPSHOT.jar --spring.profiles.active=prod
```

## 功能特性

- ✅ RESTful API 设计
- ✅ JPA/Hibernate 数据持久化
- ✅ 全局异常处理
- ✅ 请求参数验证
- ✅ CORS 跨域支持
- ✅ 多环境配置
- ✅ 单元测试
- ✅ API 响应统一格式
- ✅ 数据库事务管理

## 扩展建议

1. **安全性**: 集成 Spring Security 和 JWT
2. **缓存**: 添加 Redis 缓存支持
3. **监控**: 集成 Micrometer 和 Prometheus
4. **文档**: 添加 Swagger/OpenAPI 文档
5. **日志**: 集成 ELK 日志收集
6. **消息队列**: 集成 RabbitMQ 或 Kafka