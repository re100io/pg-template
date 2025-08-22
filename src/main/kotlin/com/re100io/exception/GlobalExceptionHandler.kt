package com.re100io.exception

import com.re100io.dto.ApiResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFound(ex: ResourceNotFoundException): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse(false, ex.message ?: "资源未找到"))
    }

    @ExceptionHandler(DuplicateResourceException::class)
    fun handleDuplicateResource(ex: DuplicateResourceException): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ApiResponse(false, ex.message ?: "资源已存在"))
    }

    @ExceptionHandler(ValidationException::class)
    fun handleValidation(ex: ValidationException): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse(false, ex.message ?: "验证失败"))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationErrors(ex: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Map<String, String>>> {
        val errors = mutableMapOf<String, String>()
        ex.bindingResult.allErrors.forEach { error ->
            val fieldName = (error as FieldError).field
            val errorMessage = error.defaultMessage ?: "验证失败"
            errors[fieldName] = errorMessage
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse(false, "请求参数验证失败", errors))
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse(false, "服务器内部错误: ${ex.message}"))
    }
}