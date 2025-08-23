package com.re100io.controller

import com.re100io.common.ApiResponse
import com.re100io.common.TraceContext
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 追踪测试控制器
 * 用于测试trace ID功能
 */
@RestController
@RequestMapping("/api/trace")
@Tag(name = "追踪测试", description = "用于测试请求追踪功能的接口")
class TraceTestController {
    
    private val logger = LoggerFactory.getLogger(TraceTestController::class.java)
    
    @GetMapping("/test")
    @Operation(summary = "测试trace ID", description = "测试请求追踪功能，返回当前的trace ID")
    fun testTrace(): ResponseEntity<ApiResponse<Map<String, String>>> {
        val traceId = TraceContext.getTraceId()
        
        logger.info("处理trace测试请求")
        logger.debug("当前trace ID: {}", traceId)
        
        val result = mapOf(
            "traceId" to (traceId ?: "未找到"),
            "message" to "trace ID测试成功"
        )
        
        return ResponseEntity.ok(ApiResponse.success(result, "获取trace ID成功"))
    }
    
    @GetMapping("/log-test")
    @Operation(summary = "测试日志追踪", description = "测试不同级别的日志输出，验证trace ID是否正确显示")
    fun testLogTrace(): ResponseEntity<ApiResponse<String>> {
        val traceId = TraceContext.getTraceId()
        
        logger.trace("TRACE级别日志测试 - TraceId: {}", traceId)
        logger.debug("DEBUG级别日志测试 - TraceId: {}", traceId)
        logger.info("INFO级别日志测试 - TraceId: {}", traceId)
        logger.warn("WARN级别日志测试 - TraceId: {}", traceId)
        
        return ResponseEntity.ok(ApiResponse.success("日志测试完成", "所有级别的日志已输出"))
    }
}