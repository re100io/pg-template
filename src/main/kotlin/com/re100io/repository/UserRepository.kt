package com.re100io.repository

import com.re100io.entity.User
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
@Mapper
interface UserRepository {
    
    // Basic CRUD operations
    fun findById(id: Long): User?
    
    fun findByUsername(username: String): User?
    
    fun findByEmail(email: String): User?
    
    fun existsByUsername(username: String): Int
    
    fun existsByEmail(email: String): Int
    
    fun existsById(id: Long): Int
    
    fun findAll(): List<User>
    
    fun findAllActiveUsers(): List<User>
    
    fun searchUsers(keyword: String): List<User>
    
    fun insert(user: User): Int
    
    fun update(user: User): Int
    
    fun deleteById(id: Long): Int
    
    // Advanced operations using XML mapper
    fun findUsersWithConditions(
        @Param("username") username: String?,
        @Param("email") email: String?,
        @Param("isActive") isActive: Boolean?,
        @Param("startDate") startDate: LocalDateTime?,
        @Param("endDate") endDate: LocalDateTime?,
        @Param("limit") limit: Int?
    ): List<User>
    
    fun batchInsert(users: List<User>): Int
    
    fun updateUserStatus(@Param("id") id: Long, @Param("isActive") isActive: Boolean): Int
    
    fun countUsersByStatus(isActive: Boolean): Int
    
    fun findByIds(ids: List<Long>): List<User>
}