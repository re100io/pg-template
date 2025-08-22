package com.re100io

import org.mybatis.spring.annotation.MapperScan
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@MapperScan("com.re100io.repository")
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
