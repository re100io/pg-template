package com.re100io.common

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

/**
 * 分页请求参数
 * 
 * @property page 页码（从0开始）
 * @property size 每页大小
 * @property sort 排序字段
 * @property direction 排序方向
 */
@Schema(description = "分页请求参数")
data class PageRequest(
    @Schema(description = "页码（从0开始）", example = "0", defaultValue = "0")
    @field:Min(value = 0, message = "页码不能小于0")
    val page: Int = 0,
    
    @Schema(description = "每页大小", example = "20", defaultValue = "20")
    @field:Min(value = 1, message = "每页大小不能小于1")
    @field:Max(value = 1000, message = "每页大小不能超过1000")
    val size: Int = 20,
    
    @Schema(description = "排序字段", example = "id")
    val sort: String? = null,
    
    @Schema(description = "排序方向", example = "ASC", allowableValues = ["ASC", "DESC"])
    val direction: SortDirection = SortDirection.ASC
) {
    /**
     * 计算偏移量
     */
    fun getOffset(): Int = page * size
    
    /**
     * 获取排序SQL片段
     */
    fun getSortSql(): String? {
        return if (sort.isNullOrBlank()) {
            null
        } else {
            "$sort ${direction.name}"
        }
    }
    
    companion object {
        /**
         * 创建默认分页请求
         */
        fun of(page: Int = 0, size: Int = 20): PageRequest {
            return PageRequest(page = page, size = size)
        }
        
        /**
         * 创建带排序的分页请求
         */
        fun of(page: Int = 0, size: Int = 20, sort: String, direction: SortDirection = SortDirection.ASC): PageRequest {
            return PageRequest(page = page, size = size, sort = sort, direction = direction)
        }
    }
}

/**
 * 排序方向枚举
 */
enum class SortDirection {
    /**
     * 升序
     */
    ASC,
    
    /**
     * 降序
     */
    DESC
}