package com.example.testProducts.models

class Product(
    var id: Int? = null,
    var title: String? = null,
    var vendor: String? = null,
    var productType: String? = null,
    var variants: MutableList<Variant> = mutableListOf()
)