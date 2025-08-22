package com.re100io.service

import com.re100io.dto.CreateUserRequest
import com.re100io.entity.User
import com.re100io.exception.DuplicateResourceException
import com.re100io.exception.ResourceNotFoundException
import com.re100io.repository.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.time.LocalDateTime
import java.util.*

class UserServiceTest {

    @Mock
    private lateinit var userRepository: UserRepository

    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        userService = UserService(userRepository)
    }

    @Test
    fun `should create user successfully`() {
        // Given
        val request = CreateUserRequest(
            username = "testuser",
            password = "password123",
            email = "test@example.com",
            fullName = "Test User"
        )
        
        val savedUser = User(
            id = 1L,
            username = request.username,
            password = request.password,
            email = request.email,
            fullName = request.fullName
        )

        `when`(userRepository.existsByUsername(request.username)).thenReturn(false)
        `when`(userRepository.existsByEmail(request.email)).thenReturn(false)
        `when`(userRepository.save(any(User::class.java))).thenReturn(savedUser)

        // When
        val result = userService.createUser(request)

        // Then
        assertEquals(savedUser.id, result.id)
        assertEquals(savedUser.username, result.username)
        assertEquals(savedUser.email, result.email)
        assertEquals(savedUser.fullName, result.fullName)
        verify(userRepository).save(any(User::class.java))
    }

    @Test
    fun `should throw exception when username already exists`() {
        // Given
        val request = CreateUserRequest(
            username = "existinguser",
            password = "password123",
            email = "test@example.com"
        )

        `when`(userRepository.existsByUsername(request.username)).thenReturn(true)

        // When & Then
        assertThrows<DuplicateResourceException> {
            userService.createUser(request)
        }
    }

    @Test
    fun `should get user by id successfully`() {
        // Given
        val userId = 1L
        val user = User(
            id = userId,
            username = "testuser",
            password = "password123",
            email = "test@example.com"
        )

        `when`(userRepository.findById(userId)).thenReturn(Optional.of(user))

        // When
        val result = userService.getUserById(userId)

        // Then
        assertEquals(user.id, result.id)
        assertEquals(user.username, result.username)
        assertEquals(user.email, result.email)
    }

    @Test
    fun `should throw exception when user not found`() {
        // Given
        val userId = 999L
        `when`(userRepository.findById(userId)).thenReturn(Optional.empty())

        // When & Then
        assertThrows<ResourceNotFoundException> {
            userService.getUserById(userId)
        }
    }
}