# Release构建指南

## 🎯 概述
本指南介绍如何构建和部署PostgreSQL Template Application的生产版本。

## 🏗️ Release构建特性

### 1. 构建优化
- **编译优化**: 启用编译器优化，禁用调试信息
- **JVM优化**: 生产环境JVM参数调优
- **依赖优化**: 排除测试依赖，优化JAR包大小
- **日志优化**: 生产环境日志级别调整

### 2. 日志级别区分
- **开发环境**: DEBUG级别，详细日志输出
- **生产环境**: WARN级别，去掉DEBUG日志

## 🚀 构建命令

### 1. 快速构建
```bash
# 构建release版本
./scripts/build-release.sh
```

### 2. 手动构建
```bash
# 清理
mvn clean

# 构建release包
mvn package -Prelease
```

### 3. Docker构建
```bash
# 构建Docker镜像
docker build -t pg-template:latest .

# 或使用部署脚本
./scripts/deploy.sh prod true
```

## 📦 构建产物

### 1. JAR文件
- `target/pg-template-1.0-SNAPSHOT.jar` - 可执行JAR包
- `target/pg-template-1.0-SNAPSHOT-sources.jar` - 源码包

### 2. 分发包
- `target/pg-template-1.0-SNAPSHOT.tar.gz` - Linux/Unix分发包
- `target/pg-template-1.0-SNAPSHOT.zip` - Windows分发包

## 🔧 运行配置

### 1. 环境配置
```bash
# 生产环境
java -jar target/pg-template-1.0-SNAPSHOT.jar --spring.profiles.active=prod

# 自定义端口
java -jar target/pg-template-1.0-SNAPSHOT.jar --server.port=8080

# 外部配置文件
java -jar target/pg-template-1.0-SNAPSHOT.jar --spring.config.location=file:./config/
```

### 2. JVM参数
```bash
# 生产环境推荐参数
java -Xmx1g -Xms1g -server -XX:+UseG1GC -XX:+UseStringDeduplication \
     -Djava.security.egd=file:/dev/./urandom \
     -jar target/pg-template-1.0-SNAPSHOT.jar
```

### 3. 环境变量
```bash
export SPRING_PROFILES_ACTIVE=prod
export SERVER_PORT=7001
export JAVA_MEMORY=1g
export DATABASE_URL=jdbc:postgresql://localhost:5432/pg_template_prod
export DATABASE_USERNAME=pg_user
export DATABASE_PASSWORD=pg_password
```

## 🐳 Docker部署

### 1. 构建镜像
```bash
# 构建镜像
docker build -t pg-template:latest .

# 查看镜像
docker images pg-template
```

### 2. 运行容器
```bash
# 单独运行
docker run -d -p 7001:7001 --name pg-template-app pg-template:latest

# 使用Docker Compose
docker-compose -f docker-compose.prod.yml up -d
```

### 3. 健康检查
```bash
# 检查容器状态
docker ps

# 查看日志
docker logs pg-template-app

# 健康检查
curl http://localhost:7001/api/health
```

## 📊 日志对比

### 开发环境 (dev/local)
```
2025-08-23 10:07:33.903 DEBUG [main] com.re100io.MainKt - Running with Spring Boot v3.4.1, Spring v6.2.1
2025-08-23 10:07:33.904  INFO [main] com.re100io.MainKt - The following 1 profile is active: "dev"
```

### 生产环境 (prod)
```
2025-08-23 10:04:47.878  INFO [main] com.re100io.MainKt - Starting MainKt using Java 21.0.6 with PID 93207
2025-08-23 10:04:47.879  INFO [main] com.re100io.MainKt - The following 1 profile is active: "prod"
```

## 🔍 监控和运维

### 1. 健康检查
```bash
# 应用健康状态
curl http://localhost:7001/api/health

# 详细健康信息
curl http://localhost:7001/api/actuator/health
```

### 2. 日志管理
```bash
# 查看应用日志
tail -f logs/application.log

# 查看错误日志
tail -f logs/error.log

# 日志轮转配置在logback-spring.xml中
```

## 🚨 故障排查

### 1. 启动问题
```bash
# 检查Java版本
java -version

# 检查端口占用
lsof -i :7001

# 检查配置文件
cat src/main/resources/application-prod.properties
```

### 2. 数据库连接问题
```bash
# 测试数据库连接
psql -h localhost -p 5432 -U pg_user -d pg_template_prod

# 检查数据库日志
docker logs pg-template-db
```

## 📋 发布检查清单

### 构建前检查
- [ ] 代码已提交到版本控制
- [ ] 所有测试通过
- [ ] 版本号已更新
- [ ] 配置文件已检查

### 构建检查
- [ ] 构建成功无错误
- [ ] JAR包大小合理
- [ ] 分发包完整

### 部署前检查
- [ ] 目标环境准备就绪
- [ ] 数据库连接正常
- [ ] 配置文件正确
- [ ] 备份已完成

### 部署后检查
- [ ] 应用启动成功
- [ ] 健康检查通过
- [ ] API接口正常
- [ ] 日志输出正常
- [ ] 性能指标正常

## 🎯 最佳实践

### 1. 版本管理
- 使用语义化版本号
- 维护详细的CHANGELOG
- 标记重要的发布版本

### 2. 配置管理
- 敏感信息使用环境变量
- 不同环境使用不同配置文件
- 配置文件版本控制

### 3. 监控告警
- 设置关键指标监控
- 配置异常告警
- 定期检查日志

### 4. 备份恢复
- 定期备份数据库
- 测试恢复流程
- 保留多个版本的备份

通过遵循本指南，可以确保PostgreSQL Template Application的稳定发布和部署。