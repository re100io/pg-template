# PostgreSQL Template Backend

基于 JDK 21、Spring Boot、MyBatis 和 PostgreSQL 的后端模板项目。

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
│   │       ├── entity/         # 数据实体
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

### 2. 环境配置

项目支持多环境配置，详见 [环境配置说明](ENVIRONMENT_SETUP.md)：

- **dev** (开发环境) - 默认激活
- **local** (本地环境) 
- **test** (测试环境)
- **prod** (生产环境)

### 3. 数据库设置

创建数据库：
```sql
CREATE DATABASE pg_template;
CREATE DATABASE pg_template_dev;
```

### 4. 快速启动

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

### 🚀 核心功能
- ✅ RESTful API设计
- ✅ 用户管理CRUD操作
- ✅ MyBatis数据持久化
- ✅ 数据验证和异常处理
- ✅ 全局异常处理器
- ✅ API响应统一格式
- ✅ 数据库事务管理

### 📚 API文档
- ✅ Swagger/OpenAPI 3.0集成
- ✅ 交互式API文档界面
- 📍 访问地址: http://localhost:8080/api/swagger-ui.html

### 🔒 安全特性
- ✅ Spring Security集成
- ✅ BCrypt密码加密
- ✅ CORS跨域支持
- ✅ 安全配置最佳实践

### 📊 监控和健康检查
- ✅ Spring Boot Actuator
- ✅ 自定义健康检查端点
- ✅ 应用性能监控

### 🚄 性能优化
- ✅ Caffeine缓存集成
- ✅ 数据库连接池优化
- ✅ JVM参数调优
- ✅ HTTP请求指标收集

### 🧪 测试支持
- ✅ 单元测试 (JUnit 5 + MockK)
- ✅ 集成测试 (TestContainers)
- ✅ 测试覆盖率报告 (JaCoCo)
- ✅ TDD开发支持

### 🐳 容器化
- ✅ 多阶段Docker构建
- ✅ Docker Compose编排
- ✅ 健康检查配置
- ✅ 非root用户运行

### 🔧 开发工具
- ✅ 开发工具脚本集合
- ✅ 代码格式化和检查
- ✅ 依赖更新检查
- ✅ 日志查看工具

### 🚀 CI/CD
- ✅ GitHub Actions工作流
- ✅ 自动化测试和构建
- ✅ 安全漏洞扫描
- ✅ 多环境部署支持

## 开发工具

项目提供了丰富的开发工具脚本：

```bash
# 查看所有可用命令
./scripts/dev-tools.sh help

# 常用开发命令
./scripts/dev-tools.sh clean          # 清理构建文件
./scripts/dev-tools.sh build         # 构建项目
./scripts/dev-tools.sh test          # 运行测试
./scripts/dev-tools.sh test-coverage # 测试覆盖率
./scripts/dev-tools.sh api-docs      # 打开API文档
./scripts/dev-tools.sh metrics       # 查看应用指标
./scripts/dev-tools.sh logs          # 查看应用日志
```

## 监控端点

启动应用后，可以访问以下监控端点：

- **API文档**: http://localhost:8080/api/swagger-ui.html
- **健康检查**: http://localhost:8080/api/health
- **应用指标**: http://localhost:8080/api/actuator/metrics

## 扩展建议

1. **认证授权**: 集成JWT令牌认证
2. **消息队列**: 集成RabbitMQ或Kafka
3. **搜索引擎**: 集成Elasticsearch
4. **文件存储**: 集成MinIO或AWS S3
5. **分布式追踪**: 集成Zipkin或Jaeger