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
        populateProductTableModel(
            model = model,
            page = 1,
            q = null,
            paginationHxTarget = "#search-table-container",
            paginationSearch = true
        )
        return "search"
    }

    @GetMapping("/products/search")
    fun searchProducts(
        @RequestParam(name = "q", required = false) q: String?,
        @RequestParam(defaultValue = "1") page: Int,
        model: Model
    ): String {
        populateProductTableModel(
            model = model,
            page = page,
            q = q,
            paginationHxTarget = "#search-table-container",
            paginationSearch = true
        )
        return "fragments :: productTable"
    }

    @GetMapping("/products")
    fun getProducts(
        @RequestParam(defaultValue = "1") page: Int,
        model: Model
    ): String {
        populateProductTableModel(
            model = model,
            page = page,
            q = null,
            paginationHxTarget = "#table-container",
            paginationSearch = false
        )
        return "fragments :: productTable"
    }

    @PostMapping("/products")
    fun addProduct(
        @RequestParam title: String,
        @RequestParam vendor: String,
        @RequestParam productType: String,
        model: Model
    ): String {
        productService.addProduct(title, vendor, productType)
        populateProductTableModel(
            model = model,
            page = 1,
            q = null,
            paginationHxTarget = "#table-container",
            paginationSearch = false
        )
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

    @PostMapping("/products/{id}/delete")
    fun deleteProduct(
        @PathVariable id: Long,
        @RequestParam(name = "q", required = false) q: String?,
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "false") fromSearch: Boolean,
        model: Model
    ): String {
        try {
            productService.deleteProduct(id)
        } catch (_: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
        val target = if (fromSearch) "#search-table-container" else "#table-container"
        populateProductTableModel(
            model = model,
            page = page,
            q = q,
            paginationHxTarget = target,
            paginationSearch = fromSearch
        )
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

    private fun populateProductTableModel(
        model: Model,
        page: Int,
        q: String?,
        paginationHxTarget: String,
        paginationSearch: Boolean
    ) {
        val result = productService.getProductsPage(page, q)
        model.addAttribute("products", result.items)
        model.addAttribute("currentPage", result.currentPage)
        model.addAttribute("totalPages", result.totalPages)
        model.addAttribute("totalItems", result.totalItems)
        model.addAttribute("pageSize", result.pageSize)
        model.addAttribute("paginationHxTarget", paginationHxTarget)
        model.addAttribute("paginationSearch", paginationSearch)
        model.addAttribute("searchQuery", q?.trim().orEmpty())
    }
}
