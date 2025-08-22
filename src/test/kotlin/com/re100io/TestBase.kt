package com.re100io

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource

/**
 * 基础测试类 - 用于单元测试
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@TestPropertySource(locations = ["classpath:application.properties"])
abstract class TestBase

/**
 * Web 测试基类 - 用于 Web 层测试
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
abstract class WebTestBase

/**
 * 集成测试基类 - 使用 TestContainers
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("integration")
@TestPropertySource(locations = ["classpath:application-integration.properties"])
abstract class IntegrationTestBase