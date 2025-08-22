package com.re100io.service

import com.re100io.TestDataFactory
import com.re100io.exception.DuplicateResourceException
import com.re100io.exception.ResourceNotFoundException
import com.re100io.repository.UserRepository
import io.mockk.*
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * UserService 简化版单元测试
 */
class UserServiceSimpleTest {

    private val userRepository = mockk<UserRepository>()
    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        userService = UserService(userRepository)
        clearAllMocks()
    }

    @Test
    fun `should create user successfully`() {
        // Given
        val request = TestDataFactory.createUserRequest()
        val savedUser = TestDataFactory.user()

        every { userRepository.existsByUsername(request.username) } returns 0
        every { userRepository.existsByEmail(request.email) } returns 0
        every { userRepository.insert(any()) } returns 1

        // When
        val result = userService.createUser(request)

        // Then
        assertThat(result.username).isEqualTo(request.username)
        assertThat(result.email).isEqualTo(request.email)
        assertThat(result.fullName).isEqualTo(request.fullName)
        assertThat(result.isActive).isTrue()

        verify { userRepository.insert(any()) }
    }

    @Test
    fun `should throw exception when username already exists`() {
        // Given
        val request = TestDataFactory.createUserRequest(username = "existinguser")

        every { userRepository.existsByUsername(request.username) } returns 1

        // When & Then
        assertThrows<DuplicateResourceException> {
            userService.createUser(request)
        }

        verify { userRepository.existsByUsername(request.username) }
        verify(exactly = 0) { userRepository.insert(any()) }
    }

    @Test
    fun `should get user by id successfully`() {
        // Given
        val userId = 1L
        val user = TestDataFactory.user(id = userId)

        every { userRepository.findById(userId) } returns user

        // When
        val result = userService.getUserById(userId)

        // Then
        assertThat(result.id).isEqualTo(user.id)
        assertThat(result.username).isEqualTo(user.username)
        assertThat(result.email).isEqualTo(user.email)
    }

    @Test
    fun `should throw exception when user not found`() {
        // Given
        val userId = 999L

        every { userRepository.findById(userId) } returns null

        // When & Then
        assertThrows<ResourceNotFoundException> {
            userService.getUserById(userId)
        }
    }

    @Test
    fun `should get all users successfully`() {
        // Given
        val users = TestDataFactory.users(3)

        every { userRepository.findAll() } returns users

        // When
        val result = userService.getAllUsers()

        // Then
        assertThat(result).hasSize(3)
        assertThat(result).allMatch { it.id != null }
        assertThat(result).allMatch { it.username.isNotBlank() }
        assertThat(result).allMatch { it.email.isNotBlank() }
    }

    @Test
    fun `should search users successfully`() {
        // Given
        val keyword = "test"
        val matchingUsers = listOf(TestDataFactory.user(username = "testuser"))

        every { userRepository.searchUsers(keyword) } returns matchingUsers

        // When
        val result = userService.searchUsers(keyword)

        // Then
        assertThat(result).hasSize(1)
        assertThat(result[0].username).isEqualTo("testuser")
    }

    @Test
    fun `should update user successfully`() {
        // Given
        val userId = 1L
        val existingUser = TestDataFactory.user(id = userId)
        val updateRequest = TestDataFactory.updateUserRequest(
            fullName = "Updated Name",
            email = "updated@example.com"
        )

        every { userRepository.findById(userId) } returns existingUser
        every { userRepository.existsByEmail(updateRequest.email!!) } returns 0
        every { userRepository.update(any()) } returns 1

        // When
        val result = userService.updateUser(userId, updateRequest)

        // Then
        assertThat(result.fullName).isEqualTo(updateRequest.fullName)
        assertThat(result.email).isEqualTo(updateRequest.email)
    }

    @Test
    fun `should delete user successfully`() {
        // Given
        val userId = 1L

        every { userRepository.existsById(userId) } returns 1
        every { userRepository.deleteById(userId) } returns 1

        // When
        userService.deleteUser(userId)

        // Then
        verify { userRepository.deleteById(userId) }
    }

    @Test
    fun `should throw exception when deleting non-existent user`() {
        // Given
        val userId = 999L

        every { userRepository.existsById(userId) } returns 0

        // When & Then
        assertThrows<ResourceNotFoundException> {
            userService.deleteUser(userId)
        }

        verify(exactly = 0) { userRepository.deleteById(any()) }
    }
}