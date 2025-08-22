package com.re100io.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.re100io.TestDataFactory
import com.re100io.dto.UserResponse
import com.re100io.exception.ResourceNotFoundException
import com.re100io.service.UserService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime

/**
 * UserController Web 层测试
 */
@WebMvcTest(UserController::class)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var userService: UserService

    @Test
    fun `should create user successfully`() {
        // Given
        val request = TestDataFactory.createUserRequest()
        val userResponse = UserResponse(
            id = 1L,
            username = request.username,
            email = request.email,
            fullName = request.fullName,
            isActive = true,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        `when`(userService.createUser(any())).thenReturn(userResponse)

        // When & Then
        mockMvc.perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("用户创建成功"))
            .andExpect(jsonPath("$.data.id").value(1))
            .andExpect(jsonPath("$.data.username").value(request.username))
            .andExpect(jsonPath("$.data.email").value(request.email))
    }

    @Test
    fun `should return validation error for invalid request`() {
        // Given
        val invalidRequest = mapOf(
            "username" to "", // 空用户名
            "password" to "123", // 密码太短
            "email" to "invalid-email" // 无效邮箱
        )

        // When & Then
        mockMvc.perform(
            post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("请求参数验证失败"))
    }

    @Test
    fun `should get user by id successfully`() {
        // Given
        val userId = 1L
        val userResponse = UserResponse(
            id = userId,
            username = "testuser",
            email = "test@example.com",
            fullName = "Test User",
            isActive = true,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        `when`(userService.getUserById(userId)).thenReturn(userResponse)

        // When & Then
        mockMvc.perform(get("/users/{id}", userId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").value(userId))
            .andExpect(jsonPath("$.data.username").value("testuser"))
    }

    @Test
    fun `should return 404 when user not found`() {
        // Given
        val userId = 999L
        `when`(userService.getUserById(userId)).thenThrow(ResourceNotFoundException("用户不存在"))

        // When & Then
        mockMvc.perform(get("/users/{id}", userId))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.success").value(false))
    }

    @Test
    fun `should get all users successfully`() {
        // Given
        val users = listOf(
            UserResponse(1L, "user1", "user1@example.com", "User 1", true, LocalDateTime.now(), LocalDateTime.now()),
            UserResponse(2L, "user2", "user2@example.com", "User 2", true, LocalDateTime.now(), LocalDateTime.now())
        )

        `when`(userService.getAllUsers()).thenReturn(users)

        // When & Then
        mockMvc.perform(get("/users"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").isArray)
            .andExpect(jsonPath("$.data.length()").value(2))
    }

    @Test
    fun `should get active users only`() {
        // Given
        val activeUsers = listOf(
            UserResponse(1L, "active1", "active1@example.com", "Active 1", true, LocalDateTime.now(), LocalDateTime.now())
        )

        `when`(userService.getActiveUsers()).thenReturn(activeUsers)

        // When & Then
        mockMvc.perform(get("/users").param("activeOnly", "true"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.length()").value(1))
    }

    @Test
    fun `should update user successfully`() {
        // Given
        val userId = 1L
        val updateRequest = TestDataFactory.updateUserRequest()
        val updatedUser = UserResponse(
            id = userId,
            username = "testuser",
            email = updateRequest.email!!,
            fullName = updateRequest.fullName,
            isActive = true,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        `when`(userService.updateUser(eq(userId), any())).thenReturn(updatedUser)

        // When & Then
        mockMvc.perform(
            put("/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.fullName").value(updateRequest.fullName))
    }

    @Test
    fun `should delete user successfully`() {
        // Given
        val userId = 1L
        doNothing().`when`(userService).deleteUser(userId)

        // When & Then
        mockMvc.perform(delete("/users/{id}", userId))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.message").value("用户删除成功"))
    }

    @Test
    fun `should search users successfully`() {
        // Given
        val keyword = "test"
        val searchResults = listOf(
            UserResponse(1L, "testuser", "test@example.com", "Test User", true, LocalDateTime.now(), LocalDateTime.now())
        )

        `when`(userService.searchUsers(keyword)).thenReturn(searchResults)

        // When & Then
        mockMvc.perform(get("/users/search").param("keyword", keyword))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.length()").value(1))
            .andExpect(jsonPath("$.data[0].username").value("testuser"))
    }
}