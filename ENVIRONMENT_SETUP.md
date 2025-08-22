# 环境配置说明

## 📋 支持的环境

项目支持以下4种环境配置：

### 1. **dev** (开发环境) - 默认
- 数据库：`pg_template_dev`
- 日志级别：DEBUG
- 启用详细的SQL日志
- 启用API文档
- 启用所有监控端点

### 2. **local** (本地环境)
- 数据库：`pg_template_local`
- 日志级别：DEBUG
- 禁用缓存便于调试
- 自动执行数据库初始化脚本
- 启用开发工具

### 3. **test** (测试环境)
- 数据库：`pg_template_test`
- 日志级别：WARN
- 使用内存数据库或测试专用数据库
- 禁用不必要的功能

### 4. **prod** (生产环境)
- 数据库：通过环境变量配置
- 日志级别：INFO/WARN
- 禁用API文档
- 限制监控端点暴露
- 优化性能配置

## 🚀 IDEA中的使用方法

### 方法1：使用预配置的运行配置
在IDEA的运行配置下拉菜单中选择：
- `Application (Dev)` - 开发环境
- `Application (Local)` - 本地环境  
- `Application (Prod)` - 生产环境

### 方法2：使用Maven运行配置
在IDEA的运行配置下拉菜单中选择：
- `Maven (Dev)` - 使用dev profile运行
- `Maven (Local)` - 使用local profile运行
- `Maven (Prod)` - 使用prod profile运行

### 方法3：手动配置Spring Boot运行配置
1. 创建新的Spring Boot运行配置
2. 在 `Active profiles` 字段中输入：`dev`, `local`, `test`, 或 `prod`
3. 在 `VM options` 中添加：`-Dspring.profiles.active=dev`

### 方法4：使用Maven profiles
在IDEA的Maven工具窗口中：
1. 展开 `Profiles` 节点
2. 选择对应的profile（dev, local, test, prod）
3. 运行 `spring-boot:run` goal

## 💻 命令行使用

### Maven命令
```bash
# 开发环境（默认）
mvn spring-boot:run

# 指定环境
mvn spring-boot:run -Pdev
mvn spring-boot:run -Plocal
mvn spring-boot:run -Ptest
mvn spring-boot:run -Pprod

# 或者使用系统属性
mvn spring-boot:run -Dspring.profiles.active=dev
```

### Java命令
```bash
# 编译后运行
java -jar target/pg-template-1.0-SNAPSHOT.jar --spring.profiles.active=dev
```

## 🔧 环境变量配置

### 生产环境推荐使用环境变量：
```bash
export DATABASE_URL=jdbc:postgresql://prod-server:5432/pg_template_prod
export DATABASE_USERNAME=prod_user
export DATABASE_PASSWORD=secure_password
export SPRING_PROFILES_ACTIVE=prod
```

## 📁 配置文件说明

| 文件 | 环境 | 说明 |
|------|------|------|
| `application.properties` | 通用 | 基础配置，所有环境共享 |
| `application-dev.properties` | 开发 | 开发环境专用配置 |
| `application-local.properties` | 本地 | 本地开发专用配置 |
| `application-test.properties` | 测试 | 测试环境专用配置 |
| `application-prod.properties` | 生产 | 生产环境专用配置 |

## 🎯 IDEA Maven插件配置

### 在IDEA中查看当前激活的Profile：
1. 打开 `View` → `Tool Windows` → `Maven`
2. 在Maven工具窗口中可以看到 `Profiles` 节点
3. 展开后可以看到所有可用的profiles
4. 勾选的profile即为当前激活的profile

### 切换Profile：
1. 在Maven工具窗口的 `Profiles` 节点中
2. 取消勾选当前profile
3. 勾选目标profile
4. 点击刷新按钮或重新导入Maven项目

## 🔍 验证配置

### 检查当前激活的Profile：
```bash
# 查看应用启动日志，会显示激活的profiles
# 或访问actuator端点
curl http://localhost:8080/api/actuator/info
```

### 数据库连接验证：
```bash
# 健康检查端点会显示数据库连接状态
curl http://localhost:8080/api/actuator/health
```

## 🛠️ 故障排除

### 1. IDEA无法识别profiles
- 确保Maven项目已正确导入
- 尝试 `File` → `Reload Maven Projects`
- 检查 `.mvn/maven.config` 文件是否存在

### 2. 运行配置不生效
- 检查运行配置中的 `Active profiles` 设置
- 确认 `VM options` 中包含正确的系统属性
- 重新创建运行配置

### 3. 配置文件不生效
- 检查文件名是否正确（application-{profile}.properties）
- 确认profile名称拼写正确
- 查看应用启动日志确认加载的配置文件

## 📝 最佳实践

1. **开发时使用 `dev` 或 `local` profile**
2. **测试时使用 `test` profile**
3. **生产部署使用 `prod` profile**
4. **敏感信息使用环境变量而非配置文件**
5. **不同环境使用不同的数据库**
6. **生产环境禁用调试功能和API文档**