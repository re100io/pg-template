package com.re100io.config

import org.springframework.boot.Banner
import org.springframework.boot.ResourceBanner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.core.io.ClassPathResource

/**
 * Banner配置类
 * 根据不同的环境配置不同的启动banner
 */
@Configuration
class BannerConfig {

    /**
     * 开发环境banner
     */
    @Bean
    @Profile("dev", "local")
    fun devBanner(): Banner {
        return ResourceBanner(ClassPathResource("banner-dev.txt"))
    }

    /**
     * 生产环境banner
     */
    @Bean
    @Profile("prod")
    fun prodBanner(): Banner {
        return ResourceBanner(ClassPathResource("banner-prod.txt"))
    }

    /**
     * 测试环境banner（使用默认的Spring Boot banner）
     */
    @Bean
    @Profile("test")
    fun testBanner(): Banner {
        return Banner { environment, sourceClass, out ->
            out.println("🧪 TEST MODE - ${environment.getProperty("spring.application.name")} v${environment.getProperty("application.version", "unknown")}")
        }
    }
}