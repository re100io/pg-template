package com.re100io.entity

import java.time.LocalDateTime

data class User(
    val id: Long? = null,
    val username: String,
    val password: String,
    val email: String,
    val fullName: String? = null,
    val isActive: Boolean = true,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)