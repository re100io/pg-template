# PostgreSQL 数据库配置指南

## 🗄️ 数据库环境

项目配置了三个独立的数据库环境：

| 环境 | 数据库名 | 用途 | DDL策略 |
|------|----------|------|---------|
| 开发 | `pg_template_dev` | 日常开发 | `create-drop` |
| 生产 | `pg_template` | 生产部署 | `validate` |
| 测试 | `pg_template_test` | 单元测试 | `create-drop` |

## 🚀 快速启动

### 方式一：使用启动脚本（推荐）

```bash
# 启动开发环境（默认）
./start.sh

# 启动生产环境
./start.sh prod

# 启动测试环境
./start.sh test

# 指定端口
./start.sh dev 8081
```

### 方式二：使用数据库管理脚本

```bash
# 启动数据库
./database/manage-db.sh start

# 查看状态
./database/manage-db.sh status

# 连接到开发数据库
./database/manage-db.sh connect pg_template_dev
```

### 方式三：手动启动

```bash
# 1. 启动数据库
docker-compose up -d postgres

# 2. 等待数据库就绪
docker exec pg-template-db pg_isready -U postgres

# 3. 构建应用
mvn clean package -DskipTests

# 4. 启动应用
java -jar target/pg-template-1.0-SNAPSHOT.jar --spring.profiles.active=dev
```

## 🔧 数据库管理

### 数据库管理命令

```bash
# 查看所有可用命令
./database/manage-db.sh help

# 启动/停止数据库
./database/manage-db.sh start
./database/manage-db.sh stop
./database/manage-db.sh restart

# 查看状态和日志
./database/manage-db.sh status
./database/manage-db.sh logs

# 连接数据库
./database/manage-db.sh connect                    # 连接生产库
./database/manage-db.sh connect pg_template_dev    # 连接开发库
./database/manage-db.sh connect pg_template_test   # 连接测试库

# 备份和恢复
./database/manage-db.sh backup pg_template         # 备份生产库
./database/manage-db.sh restore backup_file.sql   # 恢复数据库

# 重置所有数据
./database/manage-db.sh reset
```

### 直接使用 Docker 命令

```bash
# 连接到数据库容器
docker exec -it pg-template-db psql -U postgres -d pg_template_dev

# 查看数据库列表
docker exec pg-template-db psql -U postgres -c "\l"

# 查看表结构
docker exec pg-template-db psql -U postgres -d pg_template_dev -c "\dt"

# 执行 SQL 查询
docker exec pg-template-db psql -U postgres -d pg_template_dev -c "SELECT * FROM users;"
```

## 📊 连接配置

### 开发环境配置
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/pg_template_dev
spring.datasource.username=postgres
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=create-drop
```

### 生产环境配置
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/pg_template
spring.datasource.username=postgres
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=validate
```

### 测试环境配置
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/pg_template_test
spring.datasource.username=postgres
spring.datasource.password=password
spring.jpa.hibernate.ddl-auto=create-drop
```

## 🔒 安全配置

### 生产环境建议

1. **更改默认密码**：
```bash
# 在生产环境中使用环境变量
export DATABASE_PASSWORD="your_secure_password"
```

2. **创建专用用户**：
```sql
-- 连接到数据库后执行
CREATE USER app_user WITH PASSWORD 'secure_password';
GRANT CONNECT ON DATABASE pg_template TO app_user;
GRANT USAGE ON SCHEMA public TO app_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO app_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO app_user;
```

3. **限制网络访问**：
```yaml
# docker-compose.yml 生产配置
postgres:
  ports: []  # 不暴露端口到主机
  networks:
    - internal  # 仅内部网络访问
```

## 🧪 测试数据

### 插入测试用户

```bash
# 连接到开发数据库
./database/manage-db.sh connect pg_template_dev

# 插入测试数据
INSERT INTO users (username, password, email, full_name, is_active, created_at, updated_at) 
VALUES 
('admin', 'admin123', 'admin@example.com', 'Administrator', true, NOW(), NOW()),
('user1', 'password123', 'user1@example.com', 'Test User 1', true, NOW(), NOW()),
('user2', 'password123', 'user2@example.com', 'Test User 2', true, NOW(), NOW());
```

### API 测试

```bash
# 创建用户
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123",
    "email": "test@example.com",
    "fullName": "Test User"
  }'

# 获取用户列表
curl http://localhost:8080/api/users

# 搜索用户
curl "http://localhost:8080/api/users/search?keyword=test"
```

## 🚨 故障排除

### 常见问题

1. **数据库连接失败**：
```bash
# 检查数据库是否运行
docker ps | grep postgres

# 检查数据库日志
docker logs pg-template-db

# 测试连接
docker exec pg-template-db pg_isready -U postgres
```

2. **端口冲突**：
```bash
# 检查端口占用
lsof -i :5432

# 修改端口（docker-compose.yml）
ports:
  - "5433:5432"  # 使用不同端口
```

3. **权限问题**：
```bash
# 重置数据库权限
./database/manage-db.sh reset
```

4. **数据持久化问题**：
```bash
# 查看数据卷
docker volume ls | grep postgres

# 删除数据卷（注意：会丢失所有数据）
docker-compose down -v
```

## 📈 性能优化

### 连接池配置

开发环境：
- 最大连接数：10
- 最小空闲连接：5

生产环境：
- 最大连接数：20
- 最小空闲连接：10
- 连接超时：30秒
- 空闲超时：10分钟

### 监控建议

1. 启用 JPA 统计：
```properties
spring.jpa.properties.hibernate.generate_statistics=true
```

2. 使用 Actuator 监控：
```bash
curl http://localhost:8080/api/actuator/health
curl http://localhost:8080/api/actuator/metrics
```

3. 数据库监控：
```sql
-- 查看活动连接
SELECT * FROM pg_stat_activity;

-- 查看数据库大小
SELECT pg_size_pretty(pg_database_size('pg_template_dev'));
```