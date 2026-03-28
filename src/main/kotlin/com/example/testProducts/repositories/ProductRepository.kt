package com.example.testProducts.repositories

import com.example.testProducts.models.Product
import com.example.testProducts.models.Variant
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository

@Repository
class ProductRepository(
    private val jdbcClient: JdbcClient
) {

    private val productRowMapper = RowMapper { rs, _ ->
        Product(
            id = rs.getInt("id"),
            title = rs.getString("title"),
            vendor = rs.getString("vendor"),
            productType = rs.getString("product_type")
        )
    }

    private val variantRowMapper = RowMapper { rs, _ ->
        Variant(
            id = rs.getInt("id"),
            title = rs.getString("title"),
            option1 = rs.getString("option1"),
            option2 = rs.getString("option2"),
            option3 = rs.getString("option3"),
            available = rs.getBoolean("available"),
            price = rs.getBigDecimal("price")
        )
    }

    fun count(): Int {
        return jdbcClient
            .sql("SELECT COUNT(*) FROM products")
            .query(Int::class.java)
            .single()
    }

    fun saveAll(products: List<Product>) {
        products.forEach { save(it) }
    }

    fun save(product: Product) {

        val productId = jdbcClient
            .sql(
                """
                INSERT INTO products (title, vendor, product_type)
                VALUES (?, ?, ?)
                RETURNING id
                """.trimIndent()
            )
            .params(listOf(product.title, product.vendor, product.productType))
            .query(Int::class.java)
            .single()

        product.variants.forEach { variant ->
            jdbcClient
                .sql(
                    """
                    INSERT INTO variants 
                    (title, option1, option2, option3, available, price, product_id)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    """.trimIndent()
                )
                .params(listOf(
                    variant.title,
                    variant.option1,
                    variant.option2,
                    variant.option3,
                    variant.available,
                    variant.price,
                    productId)
                )
                .update()
        }
    }

    fun findAll(): List<Product> {
        val products = jdbcClient
            .sql("SELECT * FROM products ORDER BY id")
            .query(productRowMapper)
            .list()

        attachVariants(products)
        return products
    }

    fun findByTitleContaining(needle: String): List<Product> {
        val term = needle.trim()
        val products = jdbcClient
            .sql(
                """
                SELECT * FROM products
                WHERE strpos(lower(title), lower(?)) > 0
                ORDER BY id
                """.trimIndent()
            )
            .params(term)
            .query(productRowMapper)
            .list()

        attachVariants(products)
        return products
    }

    private fun attachVariants(products: List<Product>) {
        products.forEach { product ->
            val variants = jdbcClient
                .sql("SELECT * FROM variants WHERE product_id = ?")
                .params(listOf(product.id))
                .query(variantRowMapper)
                .list()

            product.variants.addAll(variants)
        }
    }

    fun update(id: Int, title: String?, vendor: String?, productType: String?) {
        jdbcClient
            .sql(
                """
                UPDATE products
                SET title = ?, vendor = ?, product_type = ?
                WHERE id = ?
                """.trimIndent()
            )
            .params(listOf(title, vendor, productType, id))
            .update()
    }

    fun deleteById(id: Int) {
        jdbcClient
            .sql("DELETE FROM products WHERE id = ?")
            .params(id)
            .update()
    }

    fun findById(id: Long): Product? {

        val product = jdbcClient
            .sql("SELECT * FROM products WHERE id = ?")
            .params(id)
            .query(productRowMapper)
            .optional()
            .orElse(null) ?: return null

        val variants = jdbcClient
            .sql("SELECT * FROM variants WHERE product_id = ?")
            .params(id)
            .query(variantRowMapper)
            .list()

        product.variants.addAll(variants)

        return product
    }
}