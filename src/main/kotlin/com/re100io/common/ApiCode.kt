package com.re100io.common

/**
 * API状态码枚举
 * 
 * 状态码规范:
 * - 200-299: 成功状态码
 * - 400-499: 客户端错误状态码
 * - 500-599: 服务端错误状态码
 * - 1000+: 业务自定义状态码
 */
enum class ApiCode(val code: Int, val message: String) {
    
    // ========== 成功状态码 (200-299) ==========
    /**
     * 操作成功
     */
    SUCCESS(200, "操作成功"),
    
    /**
     * 创建成功
     */
    CREATED(201, "创建成功"),
    
    /**
     * 更新成功
     */
    UPDATED(202, "更新成功"),
    
    /**
     * 删除成功
     */
    DELETED(204, "删除成功"),
    
    // ========== 客户端错误 (400-499) ==========
    /**
     * 请求参数错误
     */
    BAD_REQUEST(400, "请求参数错误"),
    
    /**
     * 未授权访问
     */
    UNAUTHORIZED(401, "未授权访问"),
    
    /**
     * 禁止访问
     */
    FORBIDDEN(403, "禁止访问"),
    
    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),
    
    /**
     * 请求方法不支持
     */
    METHOD_NOT_ALLOWED(405, "请求方法不支持"),
    
    /**
     * 请求超时
     */
    REQUEST_TIMEOUT(408, "请求超时"),
    
    /**
     * 资源冲突
     */
    CONFLICT(409, "资源冲突"),
    
    /**
     * 请求实体过大
     */
    PAYLOAD_TOO_LARGE(413, "请求实体过大"),
    
    /**
     * 请求频率过高
     */
    TOO_MANY_REQUESTS(429, "请求频率过高"),
    
    // ========== 服务端错误 (500-599) ==========
    /**
     * 服务器内部错误
     */
    INTERNAL_ERROR(500, "服务器内部错误"),
    
    /**
     * 功能未实现
     */
    NOT_IMPLEMENTED(501, "功能未实现"),
    
    /**
     * 服务不可用
     */
    SERVICE_UNAVAILABLE(503, "服务不可用"),
    
    /**
     * 网关超时
     */
    GATEWAY_TIMEOUT(504, "网关超时"),
    
    // ========== 业务自定义状态码 (1000+) ==========
    /**
     * 参数验证失败
     */
    VALIDATION_ERROR(1001, "参数验证失败"),
    
    /**
     * 数据库操作失败
     */
    DATABASE_ERROR(1002, "数据库操作失败"),
    
    /**
     * 外部服务调用失败
     */
    EXTERNAL_SERVICE_ERROR(1003, "外部服务调用失败"),
    
    /**
     * 缓存操作失败
     */
    CACHE_ERROR(1004, "缓存操作失败"),
    
    /**
     * 文件操作失败
     */
    FILE_ERROR(1005, "文件操作失败"),
    
    // ========== 用户相关业务状态码 (2000-2099) ==========
    /**
     * 用户不存在
     */
    USER_NOT_FOUND(2001, "用户不存在"),
    
    /**
     * 用户名已存在
     */
    USERNAME_EXISTS(2002, "用户名已存在"),
    
    /**
     * 邮箱已存在
     */
    EMAIL_EXISTS(2003, "邮箱已存在"),
    
    /**
     * 用户已被禁用
     */
    USER_DISABLED(2004, "用户已被禁用"),
    
    /**
     * 密码错误
     */
    INVALID_PASSWORD(2005, "密码错误"),
    
    /**
     * 用户状态异常
     */
    USER_STATUS_ERROR(2006, "用户状态异常"),
    
    /**
     * 用户权限不足
     */
    USER_PERMISSION_DENIED(2007, "用户权限不足"),
    
    /**
     * 用户登录过期
     */
    USER_LOGIN_EXPIRED(2008, "用户登录过期"),
    
    /**
     * 用户批量操作失败
     */
    USER_BATCH_OPERATION_FAILED(2009, "用户批量操作失败"),
    
    /**
     * 用户数据格式错误
     */
    USER_DATA_FORMAT_ERROR(2010, "用户数据格式错误"),
    
    // ========== 认证相关状态码 (3000-3099) ==========
    /**
     * Token无效
     */
    INVALID_TOKEN(3001, "Token无效"),
    
    /**
     * Token已过期
     */
    TOKEN_EXPIRED(3002, "Token已过期"),
    
    /**
     * 刷新Token失败
     */
    REFRESH_TOKEN_FAILED(3003, "刷新Token失败"),
    
    /**
     * 登录失败
     */
    LOGIN_FAILED(3004, "登录失败"),
    
    /**
     * 登出失败
     */
    LOGOUT_FAILED(3005, "登出失败"),
    
    // ========== 数据相关状态码 (4000-4099) ==========
    /**
     * 数据不存在
     */
    DATA_NOT_FOUND(4001, "数据不存在"),
    
    /**
     * 数据已存在
     */
    DATA_EXISTS(4002, "数据已存在"),
    
    /**
     * 数据格式错误
     */
    DATA_FORMAT_ERROR(4003, "数据格式错误"),
    
    /**
     * 数据完整性错误
     */
    DATA_INTEGRITY_ERROR(4004, "数据完整性错误"),
    
    /**
     * 数据版本冲突
     */
    DATA_VERSION_CONFLICT(4005, "数据版本冲突"),
    
    /**
     * 数据导入失败
     */
    DATA_IMPORT_FAILED(4006, "数据导入失败"),
    
    /**
     * 数据导出失败
     */
    DATA_EXPORT_FAILED(4007, "数据导出失败"),
    
    // ========== 系统相关状态码 (5000-5099) ==========
    /**
     * 系统维护中
     */
    SYSTEM_MAINTENANCE(5001, "系统维护中"),
    
    /**
     * 系统配置错误
     */
    SYSTEM_CONFIG_ERROR(5002, "系统配置错误"),
    
    /**
     * 系统资源不足
     */
    SYSTEM_RESOURCE_INSUFFICIENT(5003, "系统资源不足"),
    
    /**
     * 系统限流
     */
    SYSTEM_RATE_LIMIT(5004, "系统限流"),
    
    /**
     * 系统熔断
     */
    SYSTEM_CIRCUIT_BREAKER(5005, "系统熔断");
    
    companion object {
        /**
         * 根据状态码获取枚举
         */
        fun fromCode(code: Int): ApiCode? {
            return values().find { it.code == code }
        }
        
        /**
         * 判断是否为成功状态码
         */
        fun isSuccess(code: Int): Boolean {
            return code in 200..299
        }
        
        /**
         * 判断是否为客户端错误
         */
        fun isClientError(code: Int): Boolean {
            return code in 400..499
        }
        
        /**
         * 判断是否为服务端错误
         */
        fun isServerError(code: Int): Boolean {
            return code in 500..599
        }
        
        /**
         * 判断是否为业务错误
         */
        fun isBusinessError(code: Int): Boolean {
            return code >= 1000
        }
    }
}