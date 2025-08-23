package com.re100io.controller

import com.re100io.interceptor.TraceInterceptor
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(TraceTestController::class)
class TraceTestControllerTest {
    
    @Autowired
    private lateinit var mockMvc: MockMvc
    
    @Test
    fun `should return trace id in response`() {
        mockMvc.perform(get("/api/trace/test"))
            .andExpect(status().isOk)
            .andExpect(header().exists(TraceInterceptor.TRACE_ID_HEADER))
            .andExpect(header().exists(TraceInterceptor.REQUEST_ID_HEADER))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.traceId").exists())
            .andExpect(jsonPath("$.data.message").value("trace ID测试成功"))
    }
    
    @Test
    fun `should use provided trace id from header`() {
        val customTraceId = "custom-trace-id-123"
        
        mockMvc.perform(
            get("/api/trace/test")
                .header(TraceInterceptor.TRACE_ID_HEADER, customTraceId)
        )
            .andExpect(status().isOk)
            .andExpect(header().string(TraceInterceptor.TRACE_ID_HEADER, customTraceId))
            .andExpect(header().string(TraceInterceptor.REQUEST_ID_HEADER, customTraceId))
            .andExpect(jsonPath("$.data.traceId").value(customTraceId))
    }
    
    @Test
    fun `should handle log test endpoint`() {
        mockMvc.perform(get("/api/trace/log-test"))
            .andExpect(status().isOk)
            .andExpect(header().exists(TraceInterceptor.TRACE_ID_HEADER))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data").value("日志测试完成"))
            .andExpect(jsonPath("$.message").value("所有级别的日志已输出"))
    }
}