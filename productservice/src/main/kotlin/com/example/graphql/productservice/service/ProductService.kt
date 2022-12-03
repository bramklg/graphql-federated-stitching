package com.example.graphql.productservice.service

import com.example.graphql.productservice.repository.ProductRepository
import jakarta.validation.Valid
import com.example.graphql.productservice.model.Product
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ProductService(private val productRepository: ProductRepository) {

    fun byId(id: Long): Product? {
        return productRepository.findByIdOrNull(id)
    }

    fun all(): List<Product> {
        return productRepository.findAll()
    }

    fun byIds(ids: Iterable<Long>): List<Product> {
        return productRepository.findAllById(ids)
    }

    fun create(@Valid product: Product) : Product {
        return productRepository.save(product);
    }
}
