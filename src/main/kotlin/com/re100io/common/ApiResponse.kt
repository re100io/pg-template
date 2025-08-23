package com.re100io.common

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

/**
 * 统一API响应结构
 * 
 * @param T 响应数据类型
 * @property success 请求是否成功
 * @property code 业务状态码
 * @property message 响应消息
 * @property data 响应数据
 * @property timestamp 响应时间戳
 * @property traceId 请求追踪ID
 */
@Schema(description = "统一API响应结构")
@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiResponse<T>(
    @Schema(description = "请求是否成功", example = "true")
    val success: Boolean,
    
    @Schema(description = "业务状态码", example = "200")
    val code: Int,
    
    @Schema(description = "响应消息", example = "操作成功")
    val message: String,
    
    @Schema(description = "响应数据")
    val data: T? = null,
    
    @Schema(description = "响应时间戳", example = "2024-01-01T12:00:00")
    val timestamp: LocalDateTime = LocalDateTime.now(),
    
    @Schema(description = "请求追踪ID", example = "abc123")
    val traceId: String? = null
) {
    companion object {
        /**
         * 成功响应
         */
        fun <T> success(data: T? = null, message: String = "操作成功"): ApiResponse<T> {
            return ApiResponse(
                success = true,
                code = ApiCode.SUCCESS.code,
                message = message,
                data = data
            )
        }

        /**
         * 成功响应 - 带自定义状态码
         */
        fun <T> success(code: ApiCode, data: T? = null, message: String? = null): ApiResponse<T> {
            return ApiResponse(
                success = true,
                code = code.code,
                message = message ?: code.message,
                data = data
            )
        }

        /**
         * 失败响应
         */
        fun <T> error(message: String, code: Int = ApiCode.INTERNAL_ERROR.code): ApiResponse<T> {
            return ApiResponse(
                success = false,
                code = code,
                message = message,
                data = null
            )
        }

        /**
         * 失败响应 - 带状态码枚举
         */
        fun <T> error(apiCode: ApiCode, message: String? = null): ApiResponse<T> {
            return ApiResponse(
                success = false,
                code = apiCode.code,
                message = message ?: apiCode.message,
                data = null
            )
        }

        /**
         * 验证失败响应
         */
        fun <T> validationError(message: String): ApiResponse<T> {
            return ApiResponse(
                success = false,
                code = ApiCode.VALIDATION_ERROR.code,
                message = message,
                data = null
            )
        }

        /**
         * 未找到资源响应
         */
        fun <T> notFound(message: String = "资源不存在"): ApiResponse<T> {
            return ApiResponse(
                success = false,
                code = ApiCode.NOT_FOUND.code,
                message = message,
                data = null
            )
        }

        /**
         * 未授权响应
         */
        fun <T> unauthorized(message: String = "未授权访问"): ApiResponse<T> {
            return ApiResponse(
                success = false,
                code = ApiCode.UNAUTHORIZED.code,
                message = message,
                data = null
            )
        }

        /**
         * 禁止访问响应
         */
        fun <T> forbidden(message: String = "禁止访问"): ApiResponse<T> {
            return ApiResponse(
                success = false,
                code = ApiCode.FORBIDDEN.code,
                message = message,
                data = null
            )
        }
    }
}