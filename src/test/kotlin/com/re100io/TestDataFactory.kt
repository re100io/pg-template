package com.re100io

import com.re100io.dto.CreateUserRequest
import com.re100io.dto.UpdateUserRequest
import com.re100io.entity.User
import java.time.LocalDateTime

/**
 * 测试数据工厂 - 创建测试用的数据对象
 */
object TestDataFactory {

    fun createUserRequest(
        username: String = "testuser",
        password: String = "password123",
        email: String = "test@example.com",
        fullName: String? = "Test User"
    ) = CreateUserRequest(
        username = username,
        password = password,
        email = email,
        fullName = fullName
    )

    fun updateUserRequest(
        fullName: String? = "Updated User",
        email: String? = "updated@example.com"
    ) = UpdateUserRequest(
        fullName = fullName,
        email = email
    )

    fun user(
        id: Long? = 1L,
        username: String = "testuser",
        password: String = "password123",
        email: String = "test@example.com",
        fullName: String? = "Test User",
        isActive: Boolean = true,
        createdAt: LocalDateTime = LocalDateTime.now(),
        updatedAt: LocalDateTime = LocalDateTime.now()
    ) = User(
        id = id,
        username = username,
        password = password,
        email = email,
        fullName = fullName,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    fun users(count: Int = 3): List<User> {
        return (1..count).map { i ->
            user(
                id = i.toLong(),
                username = "user$i",
                email = "user$i@example.com",
                fullName = "User $i"
            )
        }
    }
}