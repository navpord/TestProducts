package com.example.testProducts.services

import com.example.testProducts.dto.ProductResponse
import com.example.testProducts.models.Product
import com.example.testProducts.models.Variant
import com.example.testProducts.repositories.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import java.math.BigDecimal

@Service
class ProductService(
    private val productRepository: ProductRepository
) {

    private val restClient = RestClient.create("https://famme.no/products.json")

    fun fetchAndSaveProducts() {
        val response = restClient.get().retrieve().body<ProductResponse>() ?: return
        if (productRepository.count() > 0) return

        val entities = response.products.take(50).map { dto ->

            val product = Product(
                title = dto.title,
                vendor = dto.vendor,
                productType = dto.productType
            )

            val variants = dto.variants.map { v ->
                Variant(
                    title = v.title,
                    option1 = v.option1,
                    option2 = v.option2,
                    option3 = v.option3,
                    available = v.available,
                    price = BigDecimal(v.price)
                )
            }

            product.variants.addAll(variants)
            product
        }

        productRepository.saveAll(entities)
    }

    fun getProducts(): List<Product> {
        return productRepository.findAll()
    }

    fun searchProductsByTitle(query: String?): List<Product> {
        val q = query?.trim().orEmpty()
        return if (q.isEmpty()) {
            productRepository.findAll()
        } else {
            productRepository.findByTitleContaining(q)
        }
    }

    fun addProduct(
        title: String,
        vendor: String,
        productType: String
    ): List<Product> {

        val product = Product(
            title = title,
            vendor = vendor,
            productType = productType
        )

        productRepository.save(product)
        return productRepository.findAll()
    }

    fun getVariants(id: Long): MutableList<Variant> {
        val product = productRepository.findById(id)
            ?: throw RuntimeException("Product not found")
        return  product.variants
    }
}