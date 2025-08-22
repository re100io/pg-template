# MyBatis 迁移完成

## 📋 迁移内容

### 1. 依赖更新
- ✅ 移除 `spring-boot-starter-data-jpa` 依赖
- ✅ 添加 `mybatis-spring-boot-starter` 依赖

### 2. 实体类更新
- ✅ 移除 JPA 注解 (`@Entity`, `@Table`, `@Id`, `@Column` 等)
- ✅ 简化为纯 Kotlin 数据类
- ✅ 保留验证注解在 DTO 层

### 3. Repository 层重构
- ✅ 从 `JpaRepository` 改为 MyBatis `@Mapper` 接口
- ✅ 使用 `@Select`, `@Insert`, `@Update`, `@Delete` 注解
- ✅ 实现所有原有的查询方法

### 4. Service 层适配
- ✅ 更新方法调用以适配 MyBatis 返回类型
- ✅ 将 `Optional<T>` 改为可空类型 `T?`
- ✅ 将 `existsBy*()` 布尔返回改为计数返回

### 5. 配置更新
- ✅ 移除 JPA/Hibernate 配置
- ✅ 添加 MyBatis 配置
- ✅ 配置 Mapper 扫描路径
- ✅ 启用下划线到驼峰命名转换

### 6. 数据库初始化
- ✅ 创建 `schema.sql` 建表脚本
- ✅ 配置自动执行 SQL 初始化

### 7. 测试更新
- ✅ 更新 Repository 测试以适配 MyBatis
- ✅ 移除 JPA 测试相关注解和依赖
- ✅ 使用 `@SpringBootTest` 进行集成测试

## 🔍 主要变化对比

### JPA 方式 (之前)
```kotlin
@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    // ...
)

interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): Optional<User>
    fun existsByUsername(username: String): Boolean
}
```

### MyBatis 方式 (现在)
```kotlin
data class User(
    val id: Long? = null,
    val username: String,
    // ...
)

@Mapper
interface UserRepository {
    @Select("SELECT * FROM users WHERE username = #{username}")
    fun findByUsername(username: String): User?
    
    @Select("SELECT COUNT(*) FROM users WHERE username = #{username}")
    fun existsByUsername(username: String): Int
}
```

## 📊 配置对比

### JPA 配置 (之前)
```properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

### MyBatis 配置 (现在)
```properties
mybatis.mapper-locations=classpath:mapper/*.xml
mybatis.type-aliases-package=com.re100io.entity
mybatis.configuration.map-underscore-to-camel-case=true
mybatis.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl
```

## 🚀 MyBatis 优势

1. **SQL 控制** - 完全控制 SQL 语句，便于优化
2. **性能** - 更直接的 SQL 映射，减少 ORM 开销
3. **灵活性** - 支持复杂查询和存储过程
4. **学习成本** - 对熟悉 SQL 的开发者更友好

## 📝 使用说明

### 添加新的查询方法
```kotlin
@Select("SELECT * FROM users WHERE created_at > #{date}")
fun findUsersCreatedAfter(date: LocalDateTime): List<User>

@Insert("INSERT INTO users (username, email) VALUES (#{username}, #{email})")
@Options(useGeneratedKeys = true, keyProperty = "id")
fun insertUser(user: User): Int
```

### 复杂查询可使用 XML 映射
在 `src/main/resources/mapper/` 目录下创建 XML 文件：
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" 
    "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.re100io.repository.UserRepository">
    <select id="complexQuery" resultType="User">
        SELECT * FROM users 
        WHERE 1=1
        <if test="username != null">
            AND username LIKE CONCAT('%', #{username}, '%')
        </if>
        <if test="isActive != null">
            AND is_active = #{isActive}
        </if>
    </select>
</mapper>
```

## ✅ 验证清单

- ✅ 应用编译成功
- ✅ 数据库连接正常
- ✅ 所有 CRUD 操作正常
- ✅ 查询方法返回正确结果
- ✅ 事务管理正常工作
- ✅ 测试用例通过

## 🔄 回滚方案

如需回滚到 JPA，可以：
1. 恢复 `spring-boot-starter-data-jpa` 依赖
2. 恢复实体类的 JPA 注解
3. 恢复 Repository 接口继承 `JpaRepository`
4. 恢复 JPA 相关配置