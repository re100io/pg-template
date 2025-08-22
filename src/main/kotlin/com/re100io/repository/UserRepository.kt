package com.re100io.repository

import com.re100io.entity.User
import org.apache.ibatis.annotations.*
import org.springframework.stereotype.Repository

@Repository
@Mapper
interface UserRepository {
    
    @Select("SELECT * FROM users WHERE id = #{id}")
    fun findById(id: Long): User?
    
    @Select("SELECT * FROM users WHERE username = #{username}")
    fun findByUsername(username: String): User?
    
    @Select("SELECT * FROM users WHERE email = #{email}")
    fun findByEmail(email: String): User?
    
    @Select("SELECT COUNT(*) FROM users WHERE username = #{username}")
    fun existsByUsername(username: String): Int
    
    @Select("SELECT COUNT(*) FROM users WHERE email = #{email}")
    fun existsByEmail(email: String): Int
    
    @Select("SELECT COUNT(*) FROM users WHERE id = #{id}")
    fun existsById(id: Long): Int
    
    @Select("SELECT * FROM users")
    fun findAll(): List<User>
    
    @Select("SELECT * FROM users WHERE is_active = true")
    fun findAllActiveUsers(): List<User>
    
    @Select("SELECT * FROM users WHERE username LIKE CONCAT('%', #{keyword}, '%') OR email LIKE CONCAT('%', #{keyword}, '%')")
    fun searchUsers(keyword: String): List<User>
    
    @Insert("""
        INSERT INTO users (username, password, email, full_name, is_active, created_at, updated_at)
        VALUES (#{username}, #{password}, #{email}, #{fullName}, #{isActive}, #{createdAt}, #{updatedAt})
    """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    fun insert(user: User): Int
    
    @Update("""
        UPDATE users SET 
            full_name = #{fullName}, 
            email = #{email}, 
            updated_at = #{updatedAt}
        WHERE id = #{id}
    """)
    fun update(user: User): Int
    
    @Delete("DELETE FROM users WHERE id = #{id}")
    fun deleteById(id: Long): Int
}