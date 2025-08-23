package com.re100io.controller

import com.re100io.common.ApiResponse
import com.re100io.dto.CreateUserRequest
import com.re100io.dto.UpdateUserRequest
import com.re100io.dto.UserResponse
import com.re100io.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
@Tag(name = "用户管理", description = "用户相关的CRUD操作接口")
class UserController(private val userService: UserService) {

    @PostMapping
    @Operation(summary = "创建用户", description = "创建一个新的用户账户")
    @ApiResponses(
        SwaggerApiResponse(responseCode = "201", description = "用户创建成功"),
        SwaggerApiResponse(responseCode = "400", description = "请求参数无效"),
        SwaggerApiResponse(responseCode = "409", description = "用户名或邮箱已存在")
    )
    @CacheEvict(value = ["users"], allEntries = true)
    fun createUser(@Valid @RequestBody request: CreateUserRequest): ResponseEntity<ApiResponse<UserResponse>> {
        val user = userService.createUser(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(user, "用户创建成功"))
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取用户", description = "通过用户ID获取用户详细信息")
    @ApiResponses(
        SwaggerApiResponse(responseCode = "200", description = "获取用户成功"),
        SwaggerApiResponse(responseCode = "404", description = "用户不存在")
    )
    @Cacheable(value = ["users"], key = "#id")
    fun getUserById(
        @Parameter(description = "用户ID", required = true)
        @PathVariable id: Long
    ): ResponseEntity<ApiResponse<UserResponse>> {
        val user = userService.getUserById(id)
        return ResponseEntity.ok(ApiResponse.success(user, "获取用户成功"))
    }

    @GetMapping
    @Operation(summary = "获取用户列表", description = "获取所有用户或仅活跃用户列表")
    @ApiResponses(
        SwaggerApiResponse(responseCode = "200", description = "获取用户列表成功")
    )
    @Cacheable(value = ["users"], key = "#activeOnly")
    fun getAllUsers(
        @Parameter(description = "是否只获取活跃用户", required = false)
        @RequestParam(defaultValue = "false") activeOnly: Boolean
    ): ResponseEntity<ApiResponse<List<UserResponse>>> {
        val users = if (activeOnly) {
            userService.getActiveUsers()
        } else {
            userService.getAllUsers()
        }
        return ResponseEntity.ok(ApiResponse.success(users, "获取用户列表成功"))
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户信息", description = "更新指定用户的信息")
    fun updateUser(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateUserRequest
    ): ResponseEntity<ApiResponse<UserResponse>> {
        val user = userService.updateUser(id, request)
        return ResponseEntity.ok(ApiResponse.success(user, "用户更新成功"))
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户", description = "删除指定的用户")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<ApiResponse<Nothing>> {
        userService.deleteUser(id)
        return ResponseEntity.ok(ApiResponse.success(message = "用户删除成功"))
    }

    @GetMapping("/search")
    @Operation(summary = "搜索用户", description = "根据关键词搜索用户")
    fun searchUsers(@RequestParam keyword: String): ResponseEntity<ApiResponse<List<UserResponse>>> {
        val users = userService.searchUsers(keyword)
        return ResponseEntity.ok(ApiResponse.success(users, "搜索用户成功"))
    }

    @GetMapping("/advanced-search")
    @Operation(summary = "高级搜索用户", description = "使用多个条件搜索用户")
    fun findUsersWithConditions(
        @RequestParam(required = false) username: String?,
        @RequestParam(required = false) email: String?,
        @RequestParam(required = false) isActive: Boolean?,
        @RequestParam(required = false) limit: Int?
    ): ResponseEntity<ApiResponse<List<UserResponse>>> {
        val users = userService.findUsersWithConditions(
            username = username,
            email = email,
            isActive = isActive,
            limit = limit
        )
        return ResponseEntity.ok(ApiResponse.success(users, "高级搜索成功"))
    }

    @PostMapping("/batch")
    @Operation(summary = "批量创建用户", description = "一次性创建多个用户")
    fun createUsersInBatch(@Valid @RequestBody requests: List<CreateUserRequest>): ResponseEntity<ApiResponse<List<UserResponse>>> {
        val users = userService.createUsersInBatch(requests)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(users, "批量创建用户成功"))
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "更新用户状态", description = "激活或禁用用户")
    fun updateUserStatus(
        @PathVariable id: Long,
        @RequestParam isActive: Boolean
    ): ResponseEntity<ApiResponse<UserResponse>> {
        val user = userService.updateUserStatus(id, isActive)
        return ResponseEntity.ok(ApiResponse.success(user, "用户状态更新成功"))
    }

    @GetMapping("/statistics")
    @Operation(summary = "获取用户统计信息", description = "获取用户数量统计")
    fun getUserStatistics(): ResponseEntity<ApiResponse<Map<String, Any>>> {
        val statistics = userService.getUserStatistics()
        return ResponseEntity.ok(ApiResponse.success(statistics, "获取统计信息成功"))
    }

    @PostMapping("/by-ids")
    @Operation(summary = "根据ID列表获取用户", description = "批量获取指定ID的用户信息")
    fun findUsersByIds(@RequestBody ids: List<Long>): ResponseEntity<ApiResponse<List<UserResponse>>> {
        val users = userService.findUsersByIds(ids)
        return ResponseEntity.ok(ApiResponse.success(users, "批量获取用户成功"))
    }
}