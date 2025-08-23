package com.re100io.controller

import com.re100io.common.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.sql.DataSource

@RestController
@RequestMapping("/api/health")
@Tag(name = "健康检查", description = "应用健康状态检查接口")
class HealthController {

    @Autowired
    private lateinit var dataSource: DataSource

    @GetMapping
    @Operation(summary = "基础健康检查", description = "检查应用基本运行状态")
    fun health(): ResponseEntity<ApiResponse<Map<String, Any>>> {
        val healthData = mutableMapOf<String, Any>(
            "status" to "UP",
            "timestamp" to System.currentTimeMillis(),
            "service" to "pg-template"
        )

        // 检查数据库连接
        try {
            dataSource.connection.use { connection ->
                val isValid = connection.isValid(5)
                healthData["database"] = mapOf(
                    "status" to if (isValid) "UP" else "DOWN",
                    "type" to "PostgreSQL"
                )
            }
        } catch (e: Exception) {
            healthData["database"] = mapOf(
                "status" to "DOWN",
                "error" to e.message
            )
        }

        return ResponseEntity.ok(
            ApiResponse.success(healthData, "健康检查完成")
        )
    }

    @GetMapping("/ready")
    @Operation(summary = "就绪检查", description = "检查应用是否准备好接收请求")
    fun readiness(): ResponseEntity<ApiResponse<Map<String, String>>> {
        // 检查关键依赖是否就绪
        try {
            dataSource.connection.use { connection ->
                if (connection.isValid(5)) {
                    return ResponseEntity.ok(
                        ApiResponse.success(mapOf("status" to "READY"), "应用已就绪")
                    )
                }
            }
        } catch (e: Exception) {
            return ResponseEntity.status(503).body(
                ApiResponse.error<Map<String, String>>("应用未就绪: ${e.message}")
            )
        }

        return ResponseEntity.status(503).body(
            ApiResponse.error<Map<String, String>>("应用未就绪")
        )
    }

    @GetMapping("/live")
    @Operation(summary = "存活检查", description = "检查应用是否存活")
    fun liveness(): ResponseEntity<ApiResponse<Map<String, String>>> {
        return ResponseEntity.ok(
            ApiResponse.success(mapOf("status" to "ALIVE"), "应用存活")
        )
    }
}