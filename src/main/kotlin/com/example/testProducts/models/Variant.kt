package com.example.testProducts.models

import java.math.BigDecimal

class Variant(
    var id: Int? = null,
    var title: String? = null,
    var option1: String? = null,
    var option2: String? = null,
    var option3: String? = null,
    var available: Boolean? = null,
    var price: BigDecimal? = null
)