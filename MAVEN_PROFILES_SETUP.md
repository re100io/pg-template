# Maven Profiles 配置完成

## 📋 配置内容

### 1. Maven Profiles 定义
在 `pom.xml` 中添加了4个环境profile：
- **dev** (开发环境) - 默认激活
- **local** (本地环境)
- **test** (测试环境) 
- **prod** (生产环境)

### 2. Spring Boot 配置文件
创建了对应的Spring Boot配置文件：
- `application-dev.properties` - 开发环境配置
- `application-local.properties` - 本地环境配置
- `application-test.properties` - 测试环境配置
- `application-prod.properties` - 生产环境配置

### 3. IDEA 运行配置
创建了预配置的IDEA运行配置：

#### Spring Boot 运行配置：
- `Application (Dev)` - 开发环境
- `Application (Local)` - 本地环境
- `Application (Prod)` - 生产环境

#### Maven 运行配置：
- `Maven (Dev)` - 使用dev profile
- `Maven (Local)` - 使用local profile
- `Maven (Prod)` - 使用prod profile

### 4. Maven 配置文件
- `.mvn/maven.config` - Maven默认配置
- 各profile的Spring Boot插件配置

## 🎯 IDEA 中的使用方法

### 方法1：使用预配置的运行配置
1. 在IDEA顶部工具栏的运行配置下拉菜单中
2. 选择对应的配置：
   - `Application (Dev)`
   - `Application (Local)`
   - `Application (Prod)`
3. 点击运行按钮

### 方法2：使用Maven工具窗口
1. 打开 `View` → `Tool Windows` → `Maven`
2. 在 `Profiles` 节点中选择目标profile
3. 在 `Plugins` → `spring-boot` 中双击 `spring-boot:run`

### 方法3：手动创建运行配置
1. `Run` → `Edit Configurations`
2. 点击 `+` 添加 `Spring Boot` 配置
3. 设置 `Active profiles` 为目标环境
4. 设置 `VM options` 为 `-Dspring.profiles.active=目标环境`

## 💻 命令行使用

### 使用Maven Profile
```bash
# 开发环境（默认）
mvn spring-boot:run

# 本地环境
mvn spring-boot:run -Plocal

# 测试环境
mvn spring-boot:run -Ptest

# 生产环境
mvn spring-boot:run -Pprod
```

### 使用Spring Profile
```bash
# 直接指定Spring Profile
mvn spring-boot:run -Dspring.profiles.active=local

# 组合使用
mvn spring-boot:run -Plocal -Dspring.profiles.active=local
```

### 查看激活的Profile
```bash
# 查看当前激活的Maven Profile
mvn help:active-profiles

# 查看特定Profile
mvn help:active-profiles -Plocal
```

## 🔧 配置特点

### Dev 环境 (默认)
- 数据库：`pg_template_dev`
- 日志级别：DEBUG
- 启用SQL日志和格式化
- 启用API文档
- 启用所有监控端点

### Local 环境
- 数据库：`pg_template_local`
- 日志级别：DEBUG
- 禁用缓存便于调试
- 自动执行数据库初始化
- 启用开发工具

### Test 环境
- 数据库：`pg_template_test`
- 日志级别：WARN
- 自动执行schema初始化
- 优化测试性能

### Prod 环境
- 数据库：通过环境变量配置
- 日志级别：INFO/WARN
- 禁用API文档和调试功能
- 限制监控端点暴露
- 性能优化配置

## 📁 文件结构

```
├── pom.xml                                    # Maven配置和profiles
├── .mvn/
│   └── maven.config                          # Maven默认配置
├── .idea/
│   └── runConfigurations/                    # IDEA运行配置
│       ├── Application_Dev.xml
│       ├── Application_Local.xml
│       ├── Application_Prod.xml
│       ├── Maven_Dev.xml
│       ├── Maven_Local.xml
│       └── Maven_Prod.xml
└── src/main/resources/
    ├── application.properties                # 通用配置
    ├── application-dev.properties           # 开发环境配置
    ├── application-local.properties         # 本地环境配置
    ├── application-test.properties          # 测试环境配置
    └── application-prod.properties          # 生产环境配置
```

## ✅ 验证方法

### 1. 检查Maven Profile
```bash
mvn help:active-profiles -Plocal
```

### 2. 检查Spring Profile
启动应用后查看日志中的：
```
The following profiles are active: local
```

### 3. 检查配置生效
```bash
# 健康检查
curl http://localhost:8080/api/actuator/health

# 查看应用信息（如果启用）
curl http://localhost:8080/api/actuator/info
```

## 🛠️ 故障排除

### 1. IDEA无法识别profiles
- 确保Maven项目已正确导入
- 执行 `File` → `Reload Maven Projects`
- 检查pom.xml语法是否正确

### 2. 运行配置不生效
- 检查运行配置中的profile设置
- 确认VM参数正确设置
- 重新导入Maven项目

### 3. 配置文件不加载
- 检查文件命名是否正确
- 确认profile名称拼写
- 查看应用启动日志

## 🚀 优势

1. **环境隔离** - 不同环境使用不同配置
2. **IDEA集成** - 预配置的运行配置，开箱即用
3. **灵活切换** - 支持多种方式切换环境
4. **团队协作** - 统一的环境配置标准
5. **部署友好** - 支持环境变量覆盖配置

现在IDEA的Maven插件可以完美识别和管理不同环境的配置了！