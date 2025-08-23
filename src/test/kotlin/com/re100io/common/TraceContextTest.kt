package com.re100io.common

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TraceContextTest {
    
    @BeforeEach
    @AfterEach
    fun cleanup() {
        TraceContext.clear()
    }
    
    @Test
    fun `should generate valid trace id`() {
        // When
        val traceId = TraceContext.generateTraceId()
        
        // Then
        assertNotNull(traceId)
        assertFalse(traceId.contains("-"))
        assertEquals(32, traceId.length) // UUID without dashes should be 32 chars
    }
    
    @Test
    fun `should set and get trace id`() {
        // Given
        val traceId = "test-trace-id-123"
        
        // When
        TraceContext.setTraceId(traceId)
        
        // Then
        assertEquals(traceId, TraceContext.getTraceId())
    }
    
    @Test
    fun `should clear trace id`() {
        // Given
        TraceContext.setTraceId("test-trace-id")
        assertNotNull(TraceContext.getTraceId())
        
        // When
        TraceContext.clearTraceId()
        
        // Then
        assertNull(TraceContext.getTraceId())
    }
    
    @Test
    fun `should extract existing trace id`() {
        // Given
        val existingTraceId = "existing-trace-123"
        
        // When
        val result = TraceContext.extractOrGenerateTraceId(existingTraceId)
        
        // Then
        assertEquals(existingTraceId, result)
    }
    
    @Test
    fun `should generate new trace id when header is null`() {
        // When
        val result = TraceContext.extractOrGenerateTraceId(null)
        
        // Then
        assertNotNull(result)
        assertEquals(32, result.length)
    }
    
    @Test
    fun `should generate new trace id when header is blank`() {
        // When
        val result = TraceContext.extractOrGenerateTraceId("   ")
        
        // Then
        assertNotNull(result)
        assertEquals(32, result.length)
    }
    
    @Test
    fun `should generate new trace id when header is empty`() {
        // When
        val result = TraceContext.extractOrGenerateTraceId("")
        
        // Then
        assertNotNull(result)
        assertEquals(32, result.length)
    }
}