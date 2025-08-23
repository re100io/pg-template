package com.re100io.exception

import com.re100io.common.ApiCode
import com.re100io.common.ApiResponse
import com.re100io.common.TraceContext
import jakarta.validation.ConstraintViolationException
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.servlet.NoHandlerFoundException
import java.sql.SQLException

/**
 * 全局异常处理器
 * 统一处理应用中的各种异常，返回标准的API响应格式
 */
@RestControllerAdvice
class GlobalExceptionHandler {
    
    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    
    /**
     * 处理业务异常
     */
    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e: BusinessException): ResponseEntity<ApiResponse<Nothing>> {
        logger.warn("业务异常: {}", e.message, e)
        val response = ApiResponse.error<Nothing>(e.code, e.message)
        return ResponseEntity.status(getHttpStatus(e.code.code)).body(response)
    }
    
    /**
     * 处理参数验证异常
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Nothing>> {
        logger.warn("参数验证异常: {}", e.message)
        val errors = e.bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
        val message = "参数验证失败: ${errors.joinToString(", ")}"
        val response = ApiResponse.error<Nothing>(ApiCode.VALIDATION_ERROR, message)
        return ResponseEntity.badRequest().body(response)
    }
    
    /**
     * 处理绑定异常
     */
    @ExceptionHandler(BindException::class)
    fun handleBindException(e: BindException): ResponseEntity<ApiResponse<Nothing>> {
        logger.warn("参数绑定异常: {}", e.message)
        val errors = e.bindingResult.fieldErrors.map { "${it.field}: ${it.defaultMessage}" }
        val message = "参数绑定失败: ${errors.joinToString(", ")}"
        val response = ApiResponse.error<Nothing>(ApiCode.VALIDATION_ERROR, message)
        return ResponseEntity.badRequest().body(response)
    }
    
    /**
     * 处理约束违反异常
     */
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(e: ConstraintViolationException): ResponseEntity<ApiResponse<Nothing>> {
        logger.warn("约束违反异常: {}", e.message)
        val errors = e.constraintViolations.map { "${it.propertyPath}: ${it.message}" }
        val message = "参数约束违反: ${errors.joinToString(", ")}"
        val response = ApiResponse.error<Nothing>(ApiCode.VALIDATION_ERROR, message)
        return ResponseEntity.badRequest().body(response)
    }
    
    /**
     * 处理缺少请求参数异常
     */
    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingParameterException(e: MissingServletRequestParameterException): ResponseEntity<ApiResponse<Nothing>> {
        logger.warn("缺少请求参数异常: {}", e.message)
        val message = "缺少必需参数: ${e.parameterName}"
        val response = ApiResponse.error<Nothing>(ApiCode.BAD_REQUEST, message)
        return ResponseEntity.badRequest().body(response)
    }
    
    /**
     * 处理参数类型不匹配异常
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatchException(e: MethodArgumentTypeMismatchException): ResponseEntity<ApiResponse<Nothing>> {
        logger.warn("参数类型不匹配异常: {}", e.message)
        val message = "参数类型错误: ${e.name} 应为 ${e.requiredType?.simpleName}"
        val response = ApiResponse.error<Nothing>(ApiCode.BAD_REQUEST, message)
        return ResponseEntity.badRequest().body(response)
    }
    
    /**
     * 处理HTTP消息不可读异常
     */
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(e: HttpMessageNotReadableException): ResponseEntity<ApiResponse<Nothing>> {
        logger.warn("HTTP消息不可读异常: {}", e.message)
        val message = "请求体格式错误"
        val response = ApiResponse.error<Nothing>(ApiCode.BAD_REQUEST, message)
        return ResponseEntity.badRequest().body(response)
    }
    
    /**
     * 处理HTTP请求方法不支持异常
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleMethodNotSupportedException(e: HttpRequestMethodNotSupportedException): ResponseEntity<ApiResponse<Nothing>> {
        logger.warn("HTTP请求方法不支持异常: {}", e.message)
        val message = "不支持的请求方法: ${e.method}"
        val response = ApiResponse.error<Nothing>(ApiCode.METHOD_NOT_ALLOWED, message)
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(response)
    }
    
    /**
     * 处理404异常
     */
    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNoHandlerFoundException(e: NoHandlerFoundException): ResponseEntity<ApiResponse<Nothing>> {
        logger.warn("404异常: {}", e.message)
        val message = "请求的资源不存在: ${e.requestURL}"
        val response = ApiResponse.error<Nothing>(ApiCode.NOT_FOUND, message)
        return ResponseEntity.notFound().build()
    }
    
    /**
     * 处理数据库重复键异常
     */
    @ExceptionHandler(DuplicateKeyException::class)
    fun handleDuplicateKeyException(e: DuplicateKeyException): ResponseEntity<ApiResponse<Nothing>> {
        logger.warn("数据库重复键异常: {}", e.message)
        val message = when {
            e.message?.contains("username", ignoreCase = true) == true -> "用户名已存在"
            e.message?.contains("email", ignoreCase = true) == true -> "邮箱已存在"
            else -> "数据已存在"
        }
        val response = ApiResponse.error<Nothing>(ApiCode.CONFLICT, message)
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response)
    }
    
    /**
     * 处理数据完整性违反异常
     */
    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handleDataIntegrityViolationException(e: DataIntegrityViolationException): ResponseEntity<ApiResponse<Nothing>> {
        logger.warn("数据完整性违反异常: {}", e.message)
        val message = "数据完整性约束违反"
        val response = ApiResponse.error<Nothing>(ApiCode.DATA_INTEGRITY_ERROR, message)
        return ResponseEntity.badRequest().body(response)
    }
    
    /**
     * 处理SQL异常
     */
    @ExceptionHandler(SQLException::class)
    fun handleSQLException(e: SQLException): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("SQL异常: {}", e.message, e)
        val message = "数据库操作失败"
        val response = ApiResponse.error<Nothing>(ApiCode.DATABASE_ERROR, message)
        return ResponseEntity.internalServerError().body(response)
    }
    
    /**
     * 处理运行时异常
     */
    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(e: RuntimeException): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("运行时异常: {}", e.message, e)
        val message = "系统运行异常"
        val response = ApiResponse.error<Nothing>(ApiCode.INTERNAL_ERROR, message)
        return ResponseEntity.internalServerError().body(response)
    }
    
    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse<Nothing>> {
        logger.error("未知异常: {}", e.message, e)
        val message = "系统内部错误"
        val response = ApiResponse.error<Nothing>(ApiCode.INTERNAL_ERROR, message)
        return ResponseEntity.internalServerError().body(response)
    }
    
    /**
     * 根据业务状态码获取HTTP状态码
     */
    private fun getHttpStatus(code: Int): HttpStatus {
        return when {
            ApiCode.isSuccess(code) -> HttpStatus.OK
            code == ApiCode.NOT_FOUND.code -> HttpStatus.NOT_FOUND
            code == ApiCode.UNAUTHORIZED.code -> HttpStatus.UNAUTHORIZED
            code == ApiCode.FORBIDDEN.code -> HttpStatus.FORBIDDEN
            code == ApiCode.CONFLICT.code -> HttpStatus.CONFLICT
            code == ApiCode.TOO_MANY_REQUESTS.code -> HttpStatus.TOO_MANY_REQUESTS
            ApiCode.isClientError(code) -> HttpStatus.BAD_REQUEST
            ApiCode.isServerError(code) -> HttpStatus.INTERNAL_SERVER_ERROR
            else -> HttpStatus.BAD_REQUEST
        }
    }
}