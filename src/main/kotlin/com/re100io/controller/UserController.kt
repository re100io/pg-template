package com.re100io.controller

import com.re100io.dto.ApiResponse
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
@RequestMapping("/users")
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
            .body(ApiResponse(true, "用户创建成功", user))
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
        return ResponseEntity.ok(ApiResponse(true, "获取用户成功", user))
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
        return ResponseEntity.ok(ApiResponse(true, "获取用户列表成功", users))
    }

    @PutMapping("/{id}")
    fun updateUser(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateUserRequest
    ): ResponseEntity<ApiResponse<UserResponse>> {
        val user = userService.updateUser(id, request)
        return ResponseEntity.ok(ApiResponse(true, "用户更新成功", user))
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<ApiResponse<Unit>> {
        userService.deleteUser(id)
        return ResponseEntity.ok(ApiResponse(true, "用户删除成功"))
    }

    @GetMapping("/search")
    fun searchUsers(@RequestParam keyword: String): ResponseEntity<ApiResponse<List<UserResponse>>> {
        val users = userService.searchUsers(keyword)
        return ResponseEntity.ok(ApiResponse(true, "搜索用户成功", users))
    }
}