package com.re100io.entity

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true, nullable = false)
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50个字符之间")
    val username: String,

    @Column(nullable = false)
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度至少6个字符")
    val password: String,

    @Column(unique = true, nullable = false)
    @Email(message = "邮箱格式不正确")
    @NotBlank(message = "邮箱不能为空")
    val email: String,

    @Column(name = "full_name")
    val fullName: String? = null,

    @Column(name = "is_active")
    val isActive: Boolean = true,

    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()
)