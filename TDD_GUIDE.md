# 测试驱动开发 (TDD) 指南

## 🎯 TDD 概述

测试驱动开发 (Test-Driven Development) 是一种软件开发方法，遵循 **红-绿-重构** 循环：

1. **红色** 🔴: 编写一个失败的测试
2. **绿色** 🟢: 编写最少的代码使测试通过
3. **重构** 🔄: 改进代码质量，保持测试通过

## 🛠️ 测试工具栈

### 单元测试
- **JUnit 5**: 测试框架
- **MockK**: Kotlin 模拟框架
- **Kotest**: BDD 风格测试
- **AssertJ**: 流畅的断言库

### 集成测试
- **TestContainers**: 容器化测试环境
- **Spring Boot Test**: Spring 集成测试
- **MockMvc**: Web 层测试

### 测试工具
- **JaCoCo**: 代码覆盖率
- **Maven Surefire**: 单元测试运行器
- **Maven Failsafe**: 集成测试运行器

## 🚀 快速开始

### 运行测试

```bash
# 运行所有测试
./test.sh all

# 只运行单元测试
./test.sh unit

# 只运行集成测试
./test.sh integration

# 生成覆盖率报告
./test.sh coverage

# TDD 模式
./test.sh tdd UserServiceTest

# 监视模式
./test.sh watch
```

### 测试结构

```
src/test/kotlin/
├── com/re100io/
│   ├── TestBase.kt              # 测试基类
│   ├── TestContainerConfig.kt   # TestContainers 配置
│   ├── TestDataFactory.kt       # 测试数据工厂
│   ├── controller/              # Controller 测试
│   ├── service/                 # Service 测试
│   ├── repository/              # Repository 测试
│   └── integration/             # 集成测试
└── resources/
    ├── application.properties           # 测试配置
    └── application-integration.properties
```

## 📝 TDD 实践指南

### 1. 编写第一个测试

```kotlin
@Test
fun `should create user with valid data`() {
    // Given (准备测试数据)
    val request = CreateUserRequest(
        username = "testuser",
        password = "password123",
        email = "test@example.com"
    )
    
    // When (执行被测试的方法)
    val result = userService.createUser(request)
    
    // Then (验证结果)
    assertThat(result.username).isEqualTo("testuser")
    assertThat(result.email).isEqualTo("test@example.com")
}
```

### 2. 使用 BDD 风格 (Kotest)

```kotlin
class UserServiceSpec : BehaviorSpec({
    given("用户服务") {
        `when`("创建新用户") {
            and("提供有效数据") {
                then("应该成功创建用户") {
                    // 测试实现
                }
            }
        }
    }
})
```

### 3. 模拟依赖 (MockK)

```kotlin
@Test
fun `should handle repository exception`() {
    // Given
    val userRepository = mockk<UserRepository>()
    every { userRepository.save(any()) } throws RuntimeException("Database error")
    
    val userService = UserService(userRepository)
    
    // When & Then
    assertThrows<RuntimeException> {
        userService.createUser(validRequest)
    }
}
```

## 🧪 测试类型

### 单元测试 (Unit Tests)

**目的**: 测试单个组件的行为  
**特点**: 快速、隔离、无外部依赖  
**命名**: `*Test.kt`, `*Spec.kt`

```kotlin
@ExtendWith(MockKExtension::class)
class UserServiceTest {
    
    @MockK
    private lateinit var userRepository: UserRepository
    
    @InjectMockKs
    private lateinit var userService: UserService
    
    @Test
    fun `should create user successfully`() {
        // 测试实现
    }
}
```

### 集成测试 (Integration Tests)

**目的**: 测试组件间的交互  
**特点**: 使用真实数据库、完整 Spring 上下文  
**命名**: `*IT.kt`, `*IntegrationTest.kt`

```kotlin
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestContainerConfig::class)
@ActiveProfiles("integration")
class UserIntegrationTest {
    
    @Autowired
    private lateinit var mockMvc: MockMvc
    
    @Test
    fun `should perform complete user lifecycle`() {
        // 测试完整的用户生命周期
    }
}
```

### Web 层测试

**目的**: 测试 REST API  
**特点**: 只加载 Web 层、模拟 Service 层

```kotlin
@WebMvcTest(UserController::class)
class UserControllerTest {
    
    @Autowired
    private lateinit var mockMvc: MockMvc
    
    @MockBean
    private lateinit var userService: UserService
    
    @Test
    fun `should return user when valid id provided`() {
        // 测试 API 端点
    }
}
```

### 数据层测试

**目的**: 测试 Repository 和数据库交互  
**特点**: 只加载 JPA 配置、使用内存数据库

```kotlin
@DataJpaTest
class UserRepositoryTest {
    
    @Autowired
    private lateinit var testEntityManager: TestEntityManager
    
    @Autowired
    private lateinit var userRepository: UserRepository
    
    @Test
    fun `should find user by username`() {
        // 测试数据库查询
    }
}
```

## 🎨 测试最佳实践

### 1. 测试命名

```kotlin
// ✅ 好的命名 - 描述性强
fun `should throw exception when username already exists`()
fun `should return empty list when no users found`()
fun `should update user email when valid email provided`()

// ❌ 不好的命名 - 不够描述性
fun testCreateUser()
fun testUserNotFound()
```

### 2. AAA 模式 (Arrange-Act-Assert)

```kotlin
@Test
fun `should calculate total price correctly`() {
    // Arrange (准备)
    val items = listOf(
        Item("book", 10.0),
        Item("pen", 2.0)
    )
    val calculator = PriceCalculator()
    
    // Act (执行)
    val total = calculator.calculateTotal(items)
    
    // Assert (断言)
    assertThat(total).isEqualTo(12.0)
}
```

### 3. 测试数据工厂

```kotlin
object TestDataFactory {
    fun createUser(
        username: String = "testuser",
        email: String = "test@example.com"
    ) = User(
        username = username,
        email = email,
        // 其他默认值...
    )
}

// 使用
val user = TestDataFactory.createUser(username = "customuser")
```

### 4. 参数化测试

```kotlin
@ParameterizedTest
@ValueSource(strings = ["", " ", "ab"])
fun `should reject invalid usernames`(username: String) {
    val request = CreateUserRequest(username = username, ...)
    
    assertThrows<ValidationException> {
        userService.createUser(request)
    }
}
```

## 📊 测试覆盖率

### 查看覆盖率报告

```bash
# 生成覆盖率报告
./test.sh coverage

# 报告位置
open target/site/jacoco/index.html
```

### 覆盖率目标

- **行覆盖率**: ≥ 80%
- **分支覆盖率**: ≥ 70%
- **方法覆盖率**: ≥ 90%

### 配置覆盖率检查

```xml
<!-- pom.xml -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <configuration>
        <rules>
            <rule>
                <limits>
                    <limit>
                        <counter>INSTRUCTION</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.80</minimum>
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</plugin>
```

## 🔄 TDD 工作流

### 1. 红色阶段 - 编写失败测试

```kotlin
@Test
fun `should calculate discount for premium users`() {
    // Given
    val user = TestDataFactory.premiumUser()
    val order = TestDataFactory.order(amount = 100.0)
    
    // When
    val discount = discountService.calculateDiscount(user, order)
    
    // Then
    assertThat(discount).isEqualTo(10.0) // 10% 折扣
}
```

**此时测试失败** ❌ - `calculateDiscount` 方法还不存在

### 2. 绿色阶段 - 编写最少代码

```kotlin
class DiscountService {
    fun calculateDiscount(user: User, order: Order): Double {
        return if (user.isPremium) {
            order.amount * 0.1
        } else {
            0.0
        }
    }
}
```

**测试通过** ✅

### 3. 重构阶段 - 改进代码

```kotlin
class DiscountService {
    companion object {
        private const val PREMIUM_DISCOUNT_RATE = 0.1
    }
    
    fun calculateDiscount(user: User, order: Order): Double {
        return when {
            user.isPremium -> order.amount * PREMIUM_DISCOUNT_RATE
            else -> 0.0
        }
    }
}
```

**测试仍然通过** ✅

## 🚨 常见问题

### 1. 测试运行缓慢

**问题**: 集成测试启动时间长  
**解决**: 
- 使用 `@MockBean` 替代真实依赖
- 使用 H2 内存数据库进行单元测试
- 合理使用 TestContainers 的容器重用

### 2. 测试不稳定

**问题**: 测试偶尔失败  
**解决**:
- 避免依赖系统时间，使用 `Clock` 抽象
- 清理测试数据，确保测试隔离
- 使用 `@Transactional` 自动回滚

### 3. 模拟过度

**问题**: 测试中模拟太多依赖  
**解决**:
- 优先使用真实对象
- 只模拟外部依赖（数据库、网络调用）
- 考虑使用测试替身（Test Double）

## 📚 进阶主题

### 1. 契约测试 (Contract Testing)

```kotlin
@Test
fun `API should match OpenAPI specification`() {
    // 验证 API 响应格式符合规范
}
```

### 2. 性能测试

```kotlin
@Test
@Timeout(value = 2, unit = TimeUnit.SECONDS)
fun `should process large dataset within time limit`() {
    // 性能测试
}
```

### 3. 安全测试

```kotlin
@Test
fun `should reject unauthorized access`() {
    mockMvc.perform(get("/admin/users"))
        .andExpect(status().isUnauthorized)
}
```

## 🎯 TDD 收益

### 开发收益
- ✅ **更好的设计**: 测试驱动更好的 API 设计
- ✅ **快速反馈**: 立即发现问题
- ✅ **重构信心**: 安全地改进代码
- ✅ **文档作用**: 测试即文档

### 质量收益
- ✅ **减少缺陷**: 早期发现问题
- ✅ **提高覆盖率**: 确保代码被测试
- ✅ **回归保护**: 防止功能退化

---

🎉 **开始你的 TDD 之旅吧！记住：先写测试，再写代码！**