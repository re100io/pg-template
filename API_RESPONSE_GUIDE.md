# API统一响应结构使用指南

## 📋 概述

本项目实现了统一的API响应结构和状态码定义，提供了一致的API接口规范，便于前端开发和API文档维护。

## 🏗️ 核心组件

### 1. ApiResponse - 统一响应结构

```kotlin
data class ApiResponse<T>(
    val success: Boolean,        // 请求是否成功
    val code: Int,              // 业务状态码
    val message: String,        // 响应消息
    val data: T? = null,        // 响应数据
    val timestamp: LocalDateTime, // 响应时间戳
    val traceId: String? = null  // 请求追踪ID
)
```

### 2. ApiCode - 状态码枚举

定义了完整的状态码体系：
- **200-299**: 成功状态码
- **400-499**: 客户端错误状态码  
- **500-599**: 服务端错误状态码
- **1000+**: 业务自定义状态码

### 3. PageResponse - 分页响应结构

```kotlin
data class PageResponse<T>(
    val content: List<T>,           // 分页数据内容
    val pagination: PaginationInfo  // 分页信息
)
```

### 4. BusinessException - 业务异常

```kotlin
class BusinessException(
    val code: ApiCode,
    message: String
) : RuntimeException(message)
```

## 🚀 使用方法

### 1. 成功响应

```kotlin
// 基础成功响应
return ResponseEntity.ok(ApiResponse.success(data, "操作成功"))

// 带自定义状态码的成功响应
return ResponseEntity.ok(ApiResponse.success(ApiCode.CREATED, data, "创建成功"))

// 无数据的成功响应
return ResponseEntity.ok(ApiResponse.success(message = "删除成功"))
```

### 2. 错误响应

```kotlin
// 通用错误响应
return ResponseEntity.badRequest().body(ApiResponse.error<Nothing>("参数错误"))

// 带状态码的错误响应
return ResponseEntity.ok(ApiResponse.error(ApiCode.USER_NOT_FOUND, "用户不存在"))

// 预定义错误响应
return ResponseEntity.ok(ApiResponse.notFound("资源不存在"))
return ResponseEntity.ok(ApiResponse.unauthorized("未授权访问"))
return ResponseEntity.ok(ApiResponse.forbidden("禁止访问"))
return ResponseEntity.ok(ApiResponse.validationError("参数验证失败"))
```

### 3. 分页响应

```kotlin
// 完整分页响应（包含总数）
val pageResponse = PageResponse.of(
    content = users,
    page = 0,
    size = 20,
    total = 100L,
    hasNext = true
)
return ResponseEntity.ok(ApiResponse.success(pageResponse, "获取分页数据成功"))

// 简单分页响应（不包含总数）
val pageResponse = PageResponse.ofSimple(
    content = users,
    page = 0,
    size = 20,
    hasNext = true
)
```

### 4. 业务异常处理

```kotlin
// 在Service层抛出业务异常
if (userRepository.existsByUsername(username) > 0) {
    throw BusinessException.usernameExists(username)
}

// 预定义的业务异常
throw BusinessException.userNotFound(userId)
throw BusinessException.emailExists(email)
throw BusinessException.userDisabled(username)
throw BusinessException.validationError("参数验证失败")
```

## 📊 状态码体系

### 成功状态码 (200-299)
- `200` - SUCCESS: 操作成功
- `201` - CREATED: 创建成功
- `202` - UPDATED: 更新成功
- `204` - DELETED: 删除成功

### 客户端错误 (400-499)
- `400` - BAD_REQUEST: 请求参数错误
- `401` - UNAUTHORIZED: 未授权访问
- `403` - FORBIDDEN: 禁止访问
- `404` - NOT_FOUND: 资源不存在
- `409` - CONFLICT: 资源冲突
- `429` - TOO_MANY_REQUESTS: 请求频率过高

### 服务端错误 (500-599)
- `500` - INTERNAL_ERROR: 服务器内部错误
- `503` - SERVICE_UNAVAILABLE: 服务不可用

### 业务错误 (1000+)
- `1001` - VALIDATION_ERROR: 参数验证失败
- `1002` - DATABASE_ERROR: 数据库操作失败
- `2001` - USER_NOT_FOUND: 用户不存在
- `2002` - USERNAME_EXISTS: 用户名已存在
- `2003` - EMAIL_EXISTS: 邮箱已存在

## 🎯 响应示例

### 成功响应示例

```json
{
  "success": true,
  "code": 200,
  "message": "获取用户成功",
  "data": {
    "id": 1,
    "username": "john_doe",
    "email": "john@example.com",
    "fullName": "John Doe",
    "isActive": true,
    "createdAt": "2024-01-01T12:00:00",
    "updatedAt": "2024-01-01T12:00:00"
  },
  "timestamp": "2024-01-01T12:00:00"
}
```

### 错误响应示例

```json
{
  "success": false,
  "code": 2001,
  "message": "用户不存在: 123",
  "data": null,
  "timestamp": "2024-01-01T12:00:00"
}
```

### 分页响应示例

```json
{
  "success": true,
  "code": 200,
  "message": "获取用户列表成功",
  "data": {
    "content": [
      {
        "id": 1,
        "username": "user1",
        "email": "user1@example.com"
      }
    ],
    "pagination": {
      "page": 0,
      "size": 20,
      "total": 100,
      "totalPages": 5,
      "hasNext": true,
      "hasPrevious": false
    }
  },
  "timestamp": "2024-01-01T12:00:00"
}
```

### 验证错误响应示例

```json
{
  "success": false,
  "code": 1001,
  "message": "参数验证失败: username: 用户名不能为空, email: 邮箱格式不正确",
  "data": null,
  "timestamp": "2024-01-01T12:00:00"
}
```

## 🛠️ 全局异常处理

项目实现了全局异常处理器 `GlobalExceptionHandler`，自动处理各种异常并返回统一格式的响应：

### 处理的异常类型

1. **BusinessException** - 业务异常
2. **MethodArgumentNotValidException** - 参数验证异常
3. **ConstraintViolationException** - 约束违反异常
4. **DuplicateKeyException** - 数据库重复键异常
5. **SQLException** - SQL异常
6. **RuntimeException** - 运行时异常
7. **Exception** - 通用异常

### 异常处理示例

```kotlin
@ExceptionHandler(BusinessException::class)
fun handleBusinessException(e: BusinessException): ResponseEntity<ApiResponse<Nothing>> {
    logger.warn("业务异常: {}", e.message, e)
    val response = ApiResponse.error<Nothing>(e.code, e.message)
    return ResponseEntity.status(getHttpStatus(e.code.code)).body(response)
}
```

## 📝 最佳实践

### 1. Controller层

```kotlin
@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @PostMapping
    fun createUser(@Valid @RequestBody request: CreateUserRequest): ResponseEntity<ApiResponse<UserResponse>> {
        val user = userService.createUser(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(user, "用户创建成功"))
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<ApiResponse<UserResponse>> {
        val user = userService.getUserById(id)
        return ResponseEntity.ok(ApiResponse.success(user, "获取用户成功"))
    }
}
```

### 2. Service层

```kotlin
@Service
class UserService(private val userRepository: UserRepository) {

    fun createUser(request: CreateUserRequest): UserResponse {
        // 业务验证
        if (userRepository.existsByUsername(request.username) > 0) {
            throw BusinessException.usernameExists(request.username)
        }
        
        // 业务逻辑
        val user = User(...)
        userRepository.insert(user)
        return mapToUserResponse(user)
    }

    fun getUserById(id: Long): UserResponse {
        val user = userRepository.findById(id)
            ?: throw BusinessException.userNotFound(id)
        return mapToUserResponse(user)
    }
}
```

### 3. 分页查询

```kotlin
@GetMapping
fun getUsers(
    @RequestParam(defaultValue = "0") page: Int,
    @RequestParam(defaultValue = "20") size: Int,
    @RequestParam(required = false) sort: String?
): ResponseEntity<ApiResponse<PageResponse<UserResponse>>> {
    val pageRequest = PageRequest.of(page, size, sort)
    val users = userService.getUsers(pageRequest)
    val pageResponse = PageResponse.of(users, page, size, totalCount)
    return ResponseEntity.ok(ApiResponse.success(pageResponse, "获取用户列表成功"))
}
```

## 🧪 测试示例

### 单元测试

```kotlin
@Test
fun `should return success response when user exists`() {
    // Given
    val userId = 1L
    val user = createTestUser(userId)
    given(userService.getUserById(userId)).willReturn(user)

    // When
    val response = userController.getUserById(userId)

    // Then
    assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
    assertThat(response.body?.success).isTrue()
    assertThat(response.body?.code).isEqualTo(200)
    assertThat(response.body?.data).isEqualTo(user)
}

@Test
fun `should return error response when user not found`() {
    // Given
    val userId = 999L
    given(userService.getUserById(userId)).willThrow(BusinessException.userNotFound(userId))

    // When & Then
    assertThatThrownBy { userController.getUserById(userId) }
        .isInstanceOf(BusinessException::class.java)
        .hasMessage("用户不存在: 999")
}
```

## 🔍 API测试

### 成功请求

```bash
curl -X GET http://localhost:8080/api/users/1
```

响应：
```json
{
  "success": true,
  "code": 200,
  "message": "获取用户成功",
  "data": { ... },
  "timestamp": "2024-01-01T12:00:00"
}
```

### 错误请求

```bash
curl -X GET http://localhost:8080/api/users/999
```

响应：
```json
{
  "success": false,
  "code": 2001,
  "message": "用户不存在: 999",
  "data": null,
  "timestamp": "2024-01-01T12:00:00"
}
```

## 📚 相关文档

- [API接口文档](http://localhost:8080/swagger-ui.html)
- [项目完整功能总结](PROJECT_SUMMARY.md)
- [开发指南](DEVELOPMENT.md)

## 🎯 总结

统一的API响应结构提供了以下优势：

1. **一致性**: 所有API接口返回相同格式的响应
2. **可预测性**: 前端可以统一处理API响应
3. **可维护性**: 集中管理状态码和错误信息
4. **可扩展性**: 易于添加新的状态码和响应类型
5. **调试友好**: 包含时间戳和追踪ID便于问题排查

通过使用这套统一的响应结构，可以大大提高API的开发效率和维护性。