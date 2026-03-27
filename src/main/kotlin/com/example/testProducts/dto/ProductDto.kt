package com.example.testProducts.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ProductDto(
    val title: String,
    val vendor: String?,
    @JsonProperty("product_type") val productType: String,
    val variants: List<VariantDto>
)