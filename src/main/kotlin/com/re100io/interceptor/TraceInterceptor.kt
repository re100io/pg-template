package com.re100io.interceptor

import com.re100io.common.TraceContext
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

/**
 * 请求追踪拦截器
 * 为每个HTTP请求生成或提取trace ID，并设置到响应头中
 */
@Component
class TraceInterceptor : HandlerInterceptor {
    
    private val logger = LoggerFactory.getLogger(TraceInterceptor::class.java)
    
    companion object {
        const val TRACE_ID_HEADER = "X-Trace-Id"
        const val REQUEST_ID_HEADER = "X-Request-Id"
    }
    
    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        // 从请求头中获取trace ID，如果没有则生成新的
        val headerTraceId = request.getHeader(TRACE_ID_HEADER) 
            ?: request.getHeader(REQUEST_ID_HEADER)
        
        val traceId = TraceContext.extractOrGenerateTraceId(headerTraceId)
        
        // 设置trace ID到MDC
        TraceContext.setTraceId(traceId)
        
        // 将trace ID添加到响应头
        response.setHeader(TRACE_ID_HEADER, traceId)
        response.setHeader(REQUEST_ID_HEADER, traceId)
        
        logger.debug("请求开始 - URI: {}, Method: {}, TraceId: {}", 
            request.requestURI, request.method, traceId)
        
        return true
    }
    
    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        val traceId = TraceContext.getTraceId()
        
        logger.debug("请求完成 - URI: {}, Status: {}, TraceId: {}", 
            request.requestURI, response.status, traceId)
        
        // 清除MDC中的trace ID
        TraceContext.clear()
    }
}