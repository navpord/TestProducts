package com.example.testProducts.utils

import com.example.testProducts.services.ProductService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ProductScheduler(
    private val productService: ProductService
) {

    @Scheduled(initialDelay = 0, fixedDelay = Long.MAX_VALUE)
    fun loadProductsAtStartup() {
        productService.fetchAndSaveProducts()
    }
}