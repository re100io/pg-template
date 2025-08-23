package com.re100io.exception

import com.re100io.common.ApiCode

/**
 * 业务异常类
 * 用于处理业务逻辑中的异常情况
 * 
 * @property code 业务状态码
 * @property message 异常消息
 * @property cause 原因异常
 */
class BusinessException : RuntimeException {
    
    val code: ApiCode
    
    constructor(code: ApiCode) : super(code.message) {
        this.code = code
    }
    
    constructor(code: ApiCode, message: String) : super(message) {
        this.code = code
    }
    
    constructor(code: ApiCode, message: String, cause: Throwable) : super(message, cause) {
        this.code = code
    }
    
    constructor(code: ApiCode, cause: Throwable) : super(code.message, cause) {
        this.code = code
    }
    
    companion object {
        /**
         * 用户不存在异常
         */
        fun userNotFound(userId: Long? = null): BusinessException {
            val message = if (userId != null) "用户不存在: $userId" else "用户不存在"
            return BusinessException(ApiCode.USER_NOT_FOUND, message)
        }
        
        /**
         * 用户名已存在异常
         */
        fun usernameExists(username: String): BusinessException {
            return BusinessException(ApiCode.USERNAME_EXISTS, "用户名已存在: $username")
        }
        
        /**
         * 邮箱已存在异常
         */
        fun emailExists(email: String): BusinessException {
            return BusinessException(ApiCode.EMAIL_EXISTS, "邮箱已存在: $email")
        }
        
        /**
         * 用户已被禁用异常
         */
        fun userDisabled(username: String): BusinessException {
            return BusinessException(ApiCode.USER_DISABLED, "用户已被禁用: $username")
        }
        
        /**
         * 密码错误异常
         */
        fun invalidPassword(): BusinessException {
            return BusinessException(ApiCode.INVALID_PASSWORD)
        }
        
        /**
         * 用户状态异常
         */
        fun userStatusError(message: String): BusinessException {
            return BusinessException(ApiCode.USER_STATUS_ERROR, message)
        }
        
        /**
         * 数据不存在异常
         */
        fun dataNotFound(message: String = "数据不存在"): BusinessException {
            return BusinessException(ApiCode.DATA_NOT_FOUND, message)
        }
        
        /**
         * 数据已存在异常
         */
        fun dataExists(message: String = "数据已存在"): BusinessException {
            return BusinessException(ApiCode.DATA_EXISTS, message)
        }
        
        /**
         * 数据格式错误异常
         */
        fun dataFormatError(message: String = "数据格式错误"): BusinessException {
            return BusinessException(ApiCode.DATA_FORMAT_ERROR, message)
        }
        
        /**
         * 参数验证失败异常
         */
        fun validationError(message: String): BusinessException {
            return BusinessException(ApiCode.VALIDATION_ERROR, message)
        }
        
        /**
         * 权限不足异常
         */
        fun permissionDenied(message: String = "权限不足"): BusinessException {
            return BusinessException(ApiCode.USER_PERMISSION_DENIED, message)
        }
        
        /**
         * 系统维护异常
         */
        fun systemMaintenance(): BusinessException {
            return BusinessException(ApiCode.SYSTEM_MAINTENANCE)
        }
        
        /**
         * 请求频率过高异常
         */
        fun tooManyRequests(): BusinessException {
            return BusinessException(ApiCode.TOO_MANY_REQUESTS)
        }
        
        /**
         * 外部服务异常
         */
        fun externalServiceError(message: String): BusinessException {
            return BusinessException(ApiCode.EXTERNAL_SERVICE_ERROR, message)
        }
    }
}