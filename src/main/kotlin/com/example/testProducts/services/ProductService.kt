package com.example.testProducts.services

import com.example.testProducts.dto.ProductResponse
import com.example.testProducts.models.Product
import com.example.testProducts.models.ProductPage
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

    companion object {
        const val PAGE_SIZE = 5
    }

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

    fun getProductsPage(page: Int, query: String?): ProductPage {
        val q = query?.trim().orEmpty()
        val totalItems = if (q.isEmpty()) {
            productRepository.count()
        } else {
            productRepository.countByTitleContaining(q)
        }

        val totalPages = if (totalItems == 0) {
            0
        } else {
            (totalItems + PAGE_SIZE - 1) / PAGE_SIZE
        }

        val safePage = when {
            totalItems == 0 -> 1
            else -> page.coerceIn(1, totalPages)
        }

        val offset = (safePage - 1) * PAGE_SIZE
        val items = if (totalItems == 0) {
            emptyList()
        } else if (q.isEmpty()) {
            productRepository.findAllPaged(offset, PAGE_SIZE)
        } else {
            productRepository.findByTitleContainingPaged(q, offset, PAGE_SIZE)
        }

        return ProductPage(
            items = items,
            currentPage = safePage,
            totalPages = totalPages,
            totalItems = totalItems,
            pageSize = PAGE_SIZE
        )
    }

    fun addProduct(
        title: String,
        vendor: String,
        productType: String
    ) {
        val product = Product(
            title = title,
            vendor = vendor,
            productType = productType
        )

        productRepository.save(product)
    }

    fun getVariants(id: Long): MutableList<Variant> {
        val product = productRepository.findById(id)
            ?: throw RuntimeException("Product not found")
        return  product.variants
    }

    fun getProduct(id: Long): Product? {
        return productRepository.findById(id)
    }

    fun updateProduct(
        id: Long,
        title: String,
        vendor: String,
        productType: String
    ) {
        val existing = productRepository.findById(id)
            ?: throw IllegalArgumentException("Product not found")
        productRepository.update(
            existing.id!!,
            title.trim(),
            vendor.trim(),
            productType.trim()
        )
    }

    fun deleteProduct(id: Long) {
        val existing = productRepository.findById(id)
            ?: throw IllegalArgumentException("Product not found")
        productRepository.deleteById(existing.id!!)
    }
}