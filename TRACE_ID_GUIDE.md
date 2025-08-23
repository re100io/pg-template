# 请求追踪 (Trace ID) 使用指南

## 概述

本项目已集成请求追踪功能，为每个HTTP请求分配唯一的trace ID，用于分布式系统中的链路追踪和日志关联。

## 功能特性

- **自动生成**: 为每个请求自动生成唯一的trace ID
- **头部传递**: 支持通过HTTP头部传递和接收trace ID
- **日志集成**: 所有日志输出都包含trace ID
- **响应头**: 在响应中返回trace ID供客户端使用
- **异常追踪**: 异常日志也包含trace ID便于问题定位

## HTTP 头部

### 请求头部
- `X-Trace-Id`: 客户端可以通过此头部传递已有的trace ID
- `X-Request-Id`: 备用头部，与X-Trace-Id功能相同

### 响应头部
- `X-Trace-Id`: 服务器返回当前请求的trace ID
- `X-Request-Id`: 与X-Trace-Id相同的值

## 使用示例

### 1. 基本请求（自动生成trace ID）

```bash
curl -X GET http://localhost:7001/api/trace/test
```

响应头部将包含：
```
X-Trace-Id: a1b2c3d4e5f6789012345678901234ab
X-Request-Id: a1b2c3d4e5f6789012345678901234ab
```

### 2. 传递已有trace ID

```bash
curl -X GET http://localhost:7001/api/trace/test \
  -H "X-Trace-Id: my-custom-trace-id-123"
```

服务器将使用提供的trace ID，并在响应中返回相同的值。

### 3. 链路追踪

在微服务架构中，客户端应该：
1. 从上游服务的响应头中获取trace ID
2. 在调用下游服务时传递该trace ID

```javascript
// JavaScript 示例
const response = await fetch('/api/users/1');
const traceId = response.headers.get('X-Trace-Id');

// 调用其他服务时传递trace ID
const nextResponse = await fetch('/api/orders', {
  headers: {
    'X-Trace-Id': traceId
  }
});
```

## 日志格式

所有日志输出都包含trace ID，格式如下：

```
2025-08-23 10:30:45.123 INFO [a1b2c3d4e5f6789012345678901234ab] com.re100io.controller.UserController - 处理获取用户请求
```

日志格式说明：
- `[a1b2c3d4e5f6789012345678901234ab]`: trace ID
- 如果没有trace ID，显示为 `[-]`

## 代码中使用

### 获取当前trace ID

```kotlin
import com.re100io.common.TraceContext

class YourService {
    fun someMethod() {
        val traceId = TraceContext.getTraceId()
        logger.info("当前trace ID: {}", traceId)
    }
}
```

### 手动设置trace ID

```kotlin
TraceContext.setTraceId("custom-trace-id")
```

### 清除trace ID

```kotlin
TraceContext.clear()
```

## 测试接口

项目提供了测试接口来验证trace ID功能：

### 1. 基本测试
```bash
GET /api/trace/test
```

返回当前请求的trace ID信息。

### 2. 日志测试
```bash
GET /api/trace/log-test
```

输出不同级别的日志，用于验证trace ID在日志中的显示。

## 配置说明

### 拦截器配置

trace ID拦截器会自动应用到所有请求，但排除以下路径：
- `/actuator/**` - 健康检查等监控接口
- `/swagger-ui/**` - Swagger UI
- `/v3/api-docs/**` - API文档
- `/swagger-resources/**` - Swagger资源
- `/webjars/**` - Web资源

### 日志配置

在 `logback-spring.xml` 中配置了trace ID的显示格式：
```xml
<pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} %5level [%X{traceId:-}] %logger{36} - %msg%n</pattern>
```

## 最佳实践

1. **客户端实现**: 前端应用应该保存并传递trace ID
2. **服务间调用**: 微服务之间调用时必须传递trace ID
3. **日志记录**: 在关键业务逻辑中添加日志，便于问题追踪
4. **异常处理**: 异常信息中会自动包含trace ID
5. **监控集成**: 可以将trace ID集成到APM系统中

## 故障排查

### 常见问题

1. **日志中没有trace ID**: 检查是否在请求上下文中
2. **trace ID不一致**: 确保在整个请求生命周期中使用同一个ID
3. **性能影响**: trace ID功能对性能影响极小，可以在生产环境安全使用

### 调试技巧

1. 使用测试接口验证功能
2. 检查响应头中的trace ID
3. 查看日志文件中的trace ID格式
4. 使用自定义trace ID进行测试

## 扩展功能

未来可以考虑的扩展：
- 集成分布式追踪系统（如Zipkin、Jaeger）
- 添加span ID支持
- 性能监控集成
- 数据库查询追踪