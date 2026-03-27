package com.example.testProducts

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TestProductsApplication

fun main(args: Array<String>) {
	runApplication<TestProductsApplication>(*args)
}
