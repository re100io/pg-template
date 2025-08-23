package com.re100io.controller

import com.re100io.common.ApiCode
import com.re100io.common.ApiResponse
import com.re100io.common.PageRequest
import com.re100io.common.PageResponse
import com.re100io.exception.BusinessException
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 示例控制器
 * 展示统一响应结构和状态码的使用方法
 */
@RestController
@RequestMapping("/api/examples")
@Tag(name = "示例接口", description = "展示统一响应结构和状态码的使用")
class ExampleController {

    @GetMapping("/success")
    @Operation(summary = "成功响应示例", description = "展示成功响应的格式")
    fun successExample(): ResponseEntity<ApiResponse<Map<String, Any>>> {
        val data = mapOf(
            "message" to "这是一个成功的响应",
            "timestamp" to System.currentTimeMillis(),
            "data" to listOf("item1", "item2", "item3")
        )
        return ResponseEntity.ok(ApiResponse.success(data))
    }

    @GetMapping("/success-with-custom-code")
    @Operation(summary = "自定义成功状态码示例", description = "展示带自定义状态码的成功响应")
    fun successWithCustomCodeExample(): ResponseEntity<ApiResponse<String>> {
        return ResponseEntity.ok(
            ApiResponse.success(ApiCode.CREATED, "资源创建成功", "自定义成功消息")
        )
    }

    @GetMapping("/error")
    @Operation(summary = "错误响应示例", description = "展示错误响应的格式")
    fun errorExample(): ResponseEntity<ApiResponse<Nothing>> {
        throw BusinessException.validationError("这是一个验证错误示例")
    }

    @GetMapping("/not-found")
    @Operation(summary = "资源不存在示例", description = "展示资源不存在的响应")
    fun notFoundExample(): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity.ok(ApiResponse.notFound("请求的资源不存在"))
    }

    @GetMapping("/unauthorized")
    @Operation(summary = "未授权示例", description = "展示未授权访问的响应")
    fun unauthorizedExample(): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity.ok(ApiResponse.unauthorized())
    }

    @GetMapping("/forbidden")
    @Operation(summary = "禁止访问示例", description = "展示禁止访问的响应")
    fun forbiddenExample(): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity.ok(ApiResponse.forbidden())
    }

    @GetMapping("/validation-error")
    @Operation(summary = "验证错误示例", description = "展示参数验证错误的响应")
    fun validationErrorExample(): ResponseEntity<ApiResponse<Nothing>> {
        return ResponseEntity.ok(ApiResponse.validationError("用户名不能为空"))
    }

    @GetMapping("/business-error")
    @Operation(summary = "业务错误示例", description = "展示业务逻辑错误的响应")
    fun businessErrorExample(): ResponseEntity<ApiResponse<Nothing>> {
        throw BusinessException.userNotFound(123L)
    }

    @GetMapping("/page")
    @Operation(summary = "分页响应示例", description = "展示分页数据的响应格式")
    fun pageExample(
        @Parameter(description = "页码") @RequestParam(defaultValue = "0") page: Int,
        @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<ApiResponse<PageResponse<Map<String, Any>>>> {
        // 模拟分页数据
        val content: List<Map<String, Any>> = (1..size).map { index ->
            mapOf<String, Any>(
                "id" to (page * size + index),
                "name" to "Item ${page * size + index}",
                "description" to "这是第 ${page * size + index} 个项目"
            )
        }
        
        val pageResponse = PageResponse.of(
            content = content,
            page = page,
            size = size,
            total = 100L,
            hasNext = page < 9
        )
        
        return ResponseEntity.ok(ApiResponse.success(pageResponse, "获取分页数据成功"))
    }

    @PostMapping("/validate")
    @Operation(summary = "参数验证示例", description = "展示参数验证的使用")
    fun validateExample(
        @Valid @RequestBody request: ExampleRequest
    ): ResponseEntity<ApiResponse<ExampleRequest>> {
        return ResponseEntity.ok(ApiResponse.success(request, "参数验证通过"))
    }

    @GetMapping("/codes")
    @Operation(summary = "状态码列表", description = "获取所有可用的状态码")
    fun getApiCodes(): ResponseEntity<ApiResponse<List<Map<String, Any>>>> {
        val codes = ApiCode.values().map { code ->
            mapOf(
                "code" to code.code,
                "name" to code.name,
                "message" to code.message,
                "isSuccess" to ApiCode.isSuccess(code.code),
                "isClientError" to ApiCode.isClientError(code.code),
                "isServerError" to ApiCode.isServerError(code.code),
                "isBusinessError" to ApiCode.isBusinessError(code.code)
            )
        }
        return ResponseEntity.ok(ApiResponse.success(codes, "获取状态码列表成功"))
    }

    @PostMapping("/batch-operation")
    @Operation(summary = "批量操作示例", description = "展示批量操作的响应格式")
    fun batchOperationExample(
        @RequestBody ids: List<Long>
    ): ResponseEntity<ApiResponse<Map<String, Any>>> {
        val result = mapOf(
            "processedCount" to ids.size,
            "successCount" to ids.size - 1,
            "failedCount" to 1,
            "failedIds" to listOf(ids.lastOrNull()),
            "details" to "批量操作完成，大部分成功"
        )
        return ResponseEntity.ok(ApiResponse.success(result, "批量操作完成"))
    }
}

/**
 * 示例请求对象
 */
data class ExampleRequest(
    @field:jakarta.validation.constraints.NotBlank(message = "名称不能为空")
    val name: String,
    
    @field:jakarta.validation.constraints.Email(message = "邮箱格式不正确")
    val email: String,
    
    @field:jakarta.validation.constraints.Min(value = 0, message = "年龄不能小于0")
    @field:jakarta.validation.constraints.Max(value = 150, message = "年龄不能大于150")
    val age: Int
)