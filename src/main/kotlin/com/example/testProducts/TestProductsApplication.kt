package com.example.testProducts

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class TestProductsApplication

fun main(args: Array<String>) {
	runApplication<TestProductsApplication>(*args)
}
