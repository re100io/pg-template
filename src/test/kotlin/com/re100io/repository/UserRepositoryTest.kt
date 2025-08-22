package com.re100io.repository

import com.re100io.TestDataFactory
import com.re100io.entity.User
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

/**
 * UserRepository MyBatis 数据访问层测试
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserRepositoryTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Test
    fun `should insert and find user by id`() {
        // Given
        val user = TestDataFactory.user(id = null, username = "testuser")
        
        // When
        userRepository.insert(user)
        val found = userRepository.findById(user.id!!)

        // Then
        assertNotNull(found)
        assertEquals("testuser", found.username)
    }

    @Test
    fun `should find user by username`() {
        // Given
        val user = TestDataFactory.user(id = null, username = "findme")
        userRepository.insert(user)

        // When
        val found = userRepository.findByUsername("findme")

        // Then
        assertNotNull(found)
        assertEquals("findme", found.username)
    }

    @Test
    fun `should find user by email`() {
        // Given
        val user = TestDataFactory.user(id = null, email = "find@example.com")
        userRepository.insert(user)

        // When
        val found = userRepository.findByEmail("find@example.com")

        // Then
        assertNotNull(found)
        assertEquals("find@example.com", found.email)
    }

    @Test
    fun `should check if username exists`() {
        // Given
        val user = TestDataFactory.user(id = null, username = "existinguser")
        userRepository.insert(user)

        // When & Then
        assertEquals(1, userRepository.existsByUsername("existinguser"))
        assertEquals(0, userRepository.existsByUsername("nonexistent"))
    }

    @Test
    fun `should check if email exists`() {
        // Given
        val user = TestDataFactory.user(id = null, email = "existing@example.com")
        userRepository.insert(user)

        // When & Then
        assertEquals(1, userRepository.existsByEmail("existing@example.com"))
        assertEquals(0, userRepository.existsByEmail("nonexistent@example.com"))
    }

    @Test
    fun `should find all active users`() {
        // Given
        val activeUser = TestDataFactory.user(id = null, username = "active", isActive = true)
        val inactiveUser = TestDataFactory.user(id = null, username = "inactive", isActive = false)
        
        userRepository.insert(activeUser)
        userRepository.insert(inactiveUser)

        // When
        val activeUsers = userRepository.findAllActiveUsers()

        // Then
        assertEquals(1, activeUsers.size)
        assertEquals("active", activeUsers[0].username)
        assertEquals(true, activeUsers[0].isActive)
    }

    @Test
    fun `should search users by keyword`() {
        // Given
        val user1 = TestDataFactory.user(id = null, username = "searchable", email = "search@example.com")
        val user2 = TestDataFactory.user(id = null, username = "another", email = "another@example.com")
        val user3 = TestDataFactory.user(id = null, username = "test", email = "searchme@example.com")
        
        userRepository.insert(user1)
        userRepository.insert(user2)
        userRepository.insert(user3)

        // When
        val results = userRepository.searchUsers("search")

        // Then
        assertEquals(2, results.size)
        assertEquals(true, results.any { it.username == "searchable" })
        assertEquals(true, results.any { it.email == "searchme@example.com" })
    }

    @Test
    fun `should update user`() {
        // Given
        val user = TestDataFactory.user(id = null, username = "updatetest", fullName = "Original Name")
        userRepository.insert(user)
        
        val updatedUser = user.copy(fullName = "Updated Name")

        // When
        userRepository.update(updatedUser)
        val found = userRepository.findById(user.id!!)

        // Then
        assertNotNull(found)
        assertEquals("Updated Name", found.fullName)
    }

    @Test
    fun `should delete user`() {
        // Given
        val user = TestDataFactory.user(id = null, username = "deletetest")
        userRepository.insert(user)

        // When
        userRepository.deleteById(user.id!!)
        val found = userRepository.findById(user.id!!)

        // Then
        assertNull(found)
    }
}