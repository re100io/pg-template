package com.re100io.service

import com.re100io.dto.CreateUserRequest
import com.re100io.dto.UpdateUserRequest
import com.re100io.dto.UserResponse
import com.re100io.entity.User
import com.re100io.exception.ResourceNotFoundException
import com.re100io.exception.DuplicateResourceException
import com.re100io.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class UserService(private val userRepository: UserRepository) {

    fun createUser(request: CreateUserRequest): UserResponse {
        if (userRepository.existsByUsername(request.username)) {
            throw DuplicateResourceException("用户名已存在: ${request.username}")
        }
        
        if (userRepository.existsByEmail(request.email)) {
            throw DuplicateResourceException("邮箱已存在: ${request.email}")
        }

        val user = User(
            username = request.username,
            password = request.password, // 实际项目中应该加密密码
            email = request.email,
            fullName = request.fullName
        )

        val savedUser = userRepository.save(user)
        return mapToUserResponse(savedUser)
    }

    @Transactional(readOnly = true)
    fun getUserById(id: Long): UserResponse {
        val user = userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("用户不存在，ID: $id") }
        return mapToUserResponse(user)
    }

    @Transactional(readOnly = true)
    fun getAllUsers(): List<UserResponse> {
        return userRepository.findAll().map { mapToUserResponse(it) }
    }

    @Transactional(readOnly = true)
    fun getActiveUsers(): List<UserResponse> {
        return userRepository.findAllActiveUsers().map { mapToUserResponse(it) }
    }

    fun updateUser(id: Long, request: UpdateUserRequest): UserResponse {
        val user = userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("用户不存在，ID: $id") }

        request.email?.let { email ->
            if (userRepository.existsByEmail(email) && user.email != email) {
                throw DuplicateResourceException("邮箱已存在: $email")
            }
        }

        val updatedUser = user.copy(
            fullName = request.fullName ?: user.fullName,
            email = request.email ?: user.email,
            updatedAt = LocalDateTime.now()
        )

        val savedUser = userRepository.save(updatedUser)
        return mapToUserResponse(savedUser)
    }

    fun deleteUser(id: Long) {
        if (!userRepository.existsById(id)) {
            throw ResourceNotFoundException("用户不存在，ID: $id")
        }
        userRepository.deleteById(id)
    }

    @Transactional(readOnly = true)
    fun searchUsers(keyword: String): List<UserResponse> {
        return userRepository.searchUsers(keyword).map { mapToUserResponse(it) }
    }

    private fun mapToUserResponse(user: User): UserResponse {
        return UserResponse(
            id = user.id!!,
            username = user.username,
            email = user.email,
            fullName = user.fullName,
            isActive = user.isActive,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt
        )
    }
}