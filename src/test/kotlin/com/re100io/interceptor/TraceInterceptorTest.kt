package com.re100io.interceptor

import com.re100io.common.TraceContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse

class TraceInterceptorTest {
    
    private lateinit var interceptor: TraceInterceptor
    private lateinit var request: MockHttpServletRequest
    private lateinit var response: MockHttpServletResponse
    
    @BeforeEach
    fun setUp() {
        interceptor = TraceInterceptor()
        request = MockHttpServletRequest()
        response = MockHttpServletResponse()
        TraceContext.clear()
    }
    
    @AfterEach
    fun tearDown() {
        TraceContext.clear()
    }
    
    @Test
    fun `should generate new trace id when no header provided`() {
        // When
        val result = interceptor.preHandle(request, response, Any())
        
        // Then
        assertTrue(result)
        assertNotNull(TraceContext.getTraceId())
        assertNotNull(response.getHeader(TraceInterceptor.TRACE_ID_HEADER))
        assertNotNull(response.getHeader(TraceInterceptor.REQUEST_ID_HEADER))
        assertEquals(TraceContext.getTraceId(), response.getHeader(TraceInterceptor.TRACE_ID_HEADER))
    }
    
    @Test
    fun `should use existing trace id from X-Trace-Id header`() {
        // Given
        val existingTraceId = "existing-trace-id-123"
        request.addHeader(TraceInterceptor.TRACE_ID_HEADER, existingTraceId)
        
        // When
        val result = interceptor.preHandle(request, response, Any())
        
        // Then
        assertTrue(result)
        assertEquals(existingTraceId, TraceContext.getTraceId())
        assertEquals(existingTraceId, response.getHeader(TraceInterceptor.TRACE_ID_HEADER))
        assertEquals(existingTraceId, response.getHeader(TraceInterceptor.REQUEST_ID_HEADER))
    }
    
    @Test
    fun `should use existing trace id from X-Request-Id header`() {
        // Given
        val existingRequestId = "existing-request-id-456"
        request.addHeader(TraceInterceptor.REQUEST_ID_HEADER, existingRequestId)
        
        // When
        val result = interceptor.preHandle(request, response, Any())
        
        // Then
        assertTrue(result)
        assertEquals(existingRequestId, TraceContext.getTraceId())
        assertEquals(existingRequestId, response.getHeader(TraceInterceptor.TRACE_ID_HEADER))
        assertEquals(existingRequestId, response.getHeader(TraceInterceptor.REQUEST_ID_HEADER))
    }
    
    @Test
    fun `should clear trace context after completion`() {
        // Given
        interceptor.preHandle(request, response, Any())
        assertNotNull(TraceContext.getTraceId())
        
        // When
        interceptor.afterCompletion(request, response, Any(), null)
        
        // Then
        assertNull(TraceContext.getTraceId())
    }
    
    @Test
    fun `should handle completion with exception`() {
        // Given
        interceptor.preHandle(request, response, Any())
        val exception = RuntimeException("Test exception")
        
        // When & Then - should not throw
        assertDoesNotThrow {
            interceptor.afterCompletion(request, response, Any(), exception)
        }
        assertNull(TraceContext.getTraceId())
    }
}