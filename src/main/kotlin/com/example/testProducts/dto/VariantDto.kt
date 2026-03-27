package com.example.testProducts.dto

data class VariantDto(
    val title: String,
    val option1: String?,
    val option2: String?,
    val option3: String?,
    val available: Boolean,
    val price: String
)