package com.re100io.common

import io.swagger.v3.oas.annotations.media.Schema

/**
 * 分页响应结构
 * 
 * @param T 数据类型
 * @property content 分页数据内容
 * @property pagination 分页信息
 */
@Schema(description = "分页响应结构")
data class PageResponse<T>(
    @Schema(description = "分页数据内容")
    val content: List<T>,
    
    @Schema(description = "分页信息")
    val pagination: PaginationInfo
) {
    companion object {
        /**
         * 创建分页响应
         */
        fun <T> of(
            content: List<T>,
            page: Int,
            size: Int,
            total: Long,
            hasNext: Boolean = false
        ): PageResponse<T> {
            return PageResponse(
                content = content,
                pagination = PaginationInfo(
                    page = page,
                    size = size,
                    total = total,
                    totalPages = if (size > 0) ((total + size - 1) / size).toInt() else 0,
                    hasNext = hasNext,
                    hasPrevious = page > 0
                )
            )
        }

        /**
         * 创建简单分页响应（不包含总数）
         */
        fun <T> ofSimple(
            content: List<T>,
            page: Int,
            size: Int,
            hasNext: Boolean = false
        ): PageResponse<T> {
            return PageResponse(
                content = content,
                pagination = PaginationInfo(
                    page = page,
                    size = size,
                    total = null,
                    totalPages = null,
                    hasNext = hasNext,
                    hasPrevious = page > 0
                )
            )
        }
    }
}

/**
 * 分页信息
 * 
 * @property page 当前页码（从0开始）
 * @property size 每页大小
 * @property total 总记录数（可选）
 * @property totalPages 总页数（可选）
 * @property hasNext 是否有下一页
 * @property hasPrevious 是否有上一页
 */
@Schema(description = "分页信息")
data class PaginationInfo(
    @Schema(description = "当前页码（从0开始）", example = "0")
    val page: Int,
    
    @Schema(description = "每页大小", example = "20")
    val size: Int,
    
    @Schema(description = "总记录数", example = "100")
    val total: Long? = null,
    
    @Schema(description = "总页数", example = "5")
    val totalPages: Int? = null,
    
    @Schema(description = "是否有下一页", example = "true")
    val hasNext: Boolean,
    
    @Schema(description = "是否有上一页", example = "false")
    val hasPrevious: Boolean
)