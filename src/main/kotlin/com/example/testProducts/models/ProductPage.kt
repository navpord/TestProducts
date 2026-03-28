package com.example.testProducts.models

data class ProductPage(
    val items: List<Product>,
    val currentPage: Int,
    val totalPages: Int,
    val totalItems: Int,
    val pageSize: Int
)