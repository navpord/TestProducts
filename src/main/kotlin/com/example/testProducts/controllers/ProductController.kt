package com.example.testProducts.controllers

import com.example.testProducts.services.ProductService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
class ProductController(
    private val productService: ProductService
) {

    @GetMapping("/")
    fun index(): String {
        return "index"
    }

    @GetMapping("/products")
    fun getProducts(model: Model): String {
        model.addAttribute("products", productService.getProducts())
        return "fragments :: productTable"
    }

    @PostMapping("/products")
    fun addProduct(
        @RequestParam title: String,
        @RequestParam vendor: String,
        @RequestParam productType: String,
        model: Model
    ): String {

        model.addAttribute("products", productService.addProduct(title, vendor, productType))
        return "fragments :: productTable"
    }

    @GetMapping("/products/{id}/variants")
    fun getVariants(
        @PathVariable id: Long,
        model: Model
    ): String {
        model.addAttribute("variants", productService.getVariants(id))
        return "fragments :: variantDialogContent"
    }
}