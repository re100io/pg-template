package com.re100io.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.servers.Server
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenApiConfig {

    @Value("\${server.port:8080}")
    private val serverPort: String = "8080"

    @Bean
    fun customOpenAPI(): OpenAPI {
        return OpenAPI()
            .info(
                Info()
                    .title("PostgreSQL Template API")
                    .description("基于Spring Boot + Kotlin + PostgreSQL的后端模板项目")
                    .version("1.0.0")
                    .contact(
                        Contact()
                            .name("RE100IO Team")
                            .email("dev@re100io.com")
                            .url("https://re100io.com")
                    )
                    .license(
                        License()
                            .name("MIT License")
                            .url("https://opensource.org/licenses/MIT")
                    )
            )
            .addServersItem(
                Server()
                    .url("http://localhost:$serverPort/api")
                    .description("开发环境")
            )
            .addServersItem(
                Server()
                    .url("https://api.re100io.com")
                    .description("生产环境")
            )
    }
}