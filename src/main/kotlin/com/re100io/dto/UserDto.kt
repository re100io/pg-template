package com.re100io.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class CreateUserRequest(
    @field:NotBlank(message = "用户名不能为空")
    @field:Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    val username: String,

    @field:NotBlank(message = "密码不能为空")
    @field:Size(min = 6, message = "密码长度至少6个字符")
    val password: String,

    @field:Email(message = "邮箱格式不正确")
    @field:NotBlank(message = "邮箱不能为空")
    val email: String,

    val fullName: String? = null
)

data class UpdateUserRequest(
    val fullName: String? = null,
    val email: String? = null
)

data class UserResponse(
    val id: Long,
    val username: String,
    val email: String,
    val fullName: String?,
    val isActive: Boolean,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null
)