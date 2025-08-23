package com.re100io.common

import org.slf4j.MDC
import java.util.*

/**
 * 请求追踪上下文工具类
 * 用于管理请求的trace ID，支持分布式链路追踪
 */
object TraceContext {
    
    private const val TRACE_ID_KEY = "traceId"
    private const val REQUEST_ID_KEY = "requestId"
    
    /**
     * 生成新的trace ID
     */
    fun generateTraceId(): String {
        return UUID.randomUUID().toString().replace("-", "")
    }
    
    /**
     * 设置trace ID到MDC
     */
    fun setTraceId(traceId: String) {
        MDC.put(TRACE_ID_KEY, traceId)
        MDC.put(REQUEST_ID_KEY, traceId)
    }
    
    /**
     * 获取当前的trace ID
     */
    fun getTraceId(): String? {
        return MDC.get(TRACE_ID_KEY)
    }
    
    /**
     * 清除trace ID
     */
    fun clearTraceId() {
        MDC.remove(TRACE_ID_KEY)
        MDC.remove(REQUEST_ID_KEY)
    }
    
    /**
     * 清除所有MDC内容
     */
    fun clear() {
        MDC.clear()
    }
    
    /**
     * 从HTTP头中提取trace ID，如果不存在则生成新的
     */
    fun extractOrGenerateTraceId(headerTraceId: String?): String {
        return if (!headerTraceId.isNullOrBlank()) {
            headerTraceId
        } else {
            generateTraceId()
        }
    }
}