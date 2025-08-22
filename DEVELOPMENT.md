# 开发指南

## 项目概述

这是一个基于Spring Boot + Kotlin + PostgreSQL的后端模板项目，提供了完整的开发脚手架和最佳实践。

## 技术栈

- **语言**: Kotlin 2.1.0
- **框架**: Spring Boot 3.4.1
- **数据库**: PostgreSQL 16
- **构建工具**: Maven 3.9+
- **JDK版本**: 21 LTS
- **容器化**: Docker & Docker Compose

## 项目结构

```
pg-template/
├── src/
│   ├── main/
│   │   ├── kotlin/com/re100io/
│   │   │   ├── config/          # 配置类
│   │   │   ├── controller/      # REST控制器
│   │   │   ├── dto/            # 数据传输对象
│   │   │   ├── entity/         # JPA实体
│   │   │   ├── exception/      # 异常处理
│   │   │   ├── repository/     # 数据访问层
│   │   │   ├── service/        # 业务逻辑层
│   │   │   └── Main.kt         # 应用入口
│   │   └── resources/
│   │       ├── application*.properties  # 配置文件
│   │       └── logback-spring.xml      # 日志配置
│   └── test/                   # 测试代码
├── database/                   # 数据库脚本
├── scripts/                    # 开发工具脚本
├── .github/workflows/          # CI/CD配置
├── docker-compose.yml          # Docker编排
├── Dockerfile                  # Docker镜像构建
└── README.md                   # 项目说明
```

## 快速开始

### 1. 环境准备

确保已安装以下工具：
- JDK 21
- Maven 3.9+
- PostgreSQL 16 (或Docker)
- Git

### 2. 克隆项目

```bash
git clone <repository-url>
cd pg-template
```

### 3. 数据库设置

#### 使用本地PostgreSQL
```bash
./database/setup-local-postgres.sh
```

#### 使用Docker
```bash
docker-compose up -d postgres
```

### 4. 启动应用

```bash
./start.sh
```

应用将在 http://localhost:8080/api 启动

## 开发工具

项目提供了丰富的开发工具脚本：

```bash
# 查看所有可用命令
./scripts/dev-tools.sh help

# 常用命令
./scripts/dev-tools.sh clean          # 清理构建文件
./scripts/dev-tools.sh test           # 运行测试
./scripts/dev-tools.sh test-coverage  # 测试覆盖率
./scripts/dev-tools.sh api-docs       # 打开API文档
./scripts/dev-tools.sh metrics        # 查看应用指标
```

## API文档

启动应用后，可以通过以下地址访问API文档：
- Swagger UI: http://localhost:8080/api/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api/v3/api-docs

## 测试

### 运行所有测试
```bash
./test.sh
```

### 运行特定测试
```bash
mvn test -Dtest=UserServiceTest
```

### 测试覆盖率
```bash
./scripts/dev-tools.sh test-coverage
```

## 代码质量

### 代码格式化
```bash
./scripts/dev-tools.sh format
```

### 代码检查
```bash
./scripts/dev-tools.sh lint
```

## 监控和健康检查

### 健康检查端点
- 基础健康检查: `GET /api/health`
- 详细健康检查: `GET /api/health/detailed`
- 就绪检查: `GET /api/health/ready`
- 存活检查: `GET /api/health/live`

### 监控指标
- Actuator端点: http://localhost:8080/api/actuator
- 应用指标: http://localhost:8080/api/actuator/metrics

## 配置管理

项目支持多环境配置：
- `application.properties` - 基础配置
- `application-dev.properties` - 开发环境
- `application-test.properties` - 测试环境
- `application-prod.properties` - 生产环境

### 切换环境
```bash
# 开发环境（默认）
./start.sh dev

# 测试环境
./start.sh test

# 生产环境
./start.sh prod
```

## 数据库管理

### 数据库操作
```bash
# 重置数据库
./database/manage-db.sh reset

# 创建数据库
./database/manage-db.sh create

# 删除数据库
./database/manage-db.sh drop
```

## Docker部署

### 构建镜像
```bash
docker build -t pg-template .
```

### 使用Docker Compose
```bash
# 启动所有服务
docker-compose up -d

# 查看日志
docker-compose logs -f app

# 停止服务
docker-compose down
```

## 安全配置

项目集成了Spring Security，当前配置：
- 开放端点：健康检查、API文档、用户API（开发阶段）
- CORS支持：允许跨域请求
- 密码加密：使用BCrypt

## 缓存策略

使用Caffeine缓存：
- 用户数据缓存：10分钟过期
- 最大缓存条目：1000个
- 支持缓存统计

## 日志配置

- 开发环境：控制台 + 文件输出，DEBUG级别
- 测试环境：控制台输出，INFO级别
- 生产环境：文件输出，INFO级别，错误日志单独文件

## 性能优化

### JVM参数
```bash
-Xms512m -Xmx1024m -XX:+UseG1GC -XX:+UseContainerSupport
```

### 数据库连接池
- HikariCP连接池
- 最大连接数：10
- 最小空闲连接：5

## 故障排除

### 常见问题

1. **端口被占用**
   ```bash
   lsof -ti:8080 | xargs kill -9
   ```

2. **数据库连接失败**
   - 检查PostgreSQL是否运行
   - 验证数据库配置
   - 确认数据库已创建

3. **Maven构建失败**
   ```bash
   mvn clean install -U
   ```

4. **测试失败**
   - 检查测试数据库配置
   - 确保测试环境隔离

### 日志查看
```bash
# 实时查看日志
./scripts/dev-tools.sh logs

# 查看错误日志
tail -f logs/app-error.log
```

## 贡献指南

1. Fork项目
2. 创建功能分支：`git checkout -b feature/new-feature`
3. 提交更改：`git commit -am 'Add new feature'`
4. 推送分支：`git push origin feature/new-feature`
5. 创建Pull Request

## 许可证

MIT License - 详见 [LICENSE](LICENSE) 文件