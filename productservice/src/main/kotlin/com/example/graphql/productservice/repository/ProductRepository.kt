package com.example.graphql.productservice.repository

import com.example.graphql.productservice.model.Product
import org.springframework.data.repository.ListCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductRepository : ListCrudRepository<Product, Long> {
}
