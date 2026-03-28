package com.example.testProducts.controllers

import com.example.testProducts.services.ProductService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@Controller
class ProductController(
    private val productService: ProductService
) {

    @GetMapping("/")
    fun index(): String {
        return "index"
    }

    @GetMapping("/search")
    fun searchPage(model: Model): String {
        model.addAttribute("products", productService.getProducts())
        return "search"
    }

    @GetMapping("/products/search")
    fun searchProducts(
        @RequestParam(name = "q", required = false) q: String?,
        model: Model
    ): String {
        model.addAttribute("products", productService.searchProductsByTitle(q))
        return "fragments :: productTable"
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

    @GetMapping("/products/{id}/edit")
    fun editProductPage(@PathVariable id: Long, model: Model): String {
        val product = productService.getProduct(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        model.addAttribute("product", product)
        return "edit"
    }

    @PostMapping("/products/{id}")
    fun updateProduct(
        @PathVariable id: Long,
        @RequestParam title: String,
        @RequestParam vendor: String,
        @RequestParam productType: String
    ): String {
        try {
            productService.updateProduct(id, title, vendor, productType)
        } catch (_: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
        return "redirect:/"
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