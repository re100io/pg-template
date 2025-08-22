package com.re100io.controller

import com.re100io.dto.ApiResponse
import com.re100io.dto.CreateUserRequest
import com.re100io.dto.UpdateUserRequest
import com.re100io.dto.UserResponse
import com.re100io.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(private val userService: UserService) {

    @PostMapping
    fun createUser(@Valid @RequestBody request: CreateUserRequest): ResponseEntity<ApiResponse<UserResponse>> {
        val user = userService.createUser(request)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse(true, "用户创建成功", user))
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<ApiResponse<UserResponse>> {
        val user = userService.getUserById(id)
        return ResponseEntity.ok(ApiResponse(true, "获取用户成功", user))
    }

    @GetMapping
    fun getAllUsers(@RequestParam(defaultValue = "false") activeOnly: Boolean): ResponseEntity<ApiResponse<List<UserResponse>>> {
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