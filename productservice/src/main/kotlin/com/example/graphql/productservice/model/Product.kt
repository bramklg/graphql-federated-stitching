package com.example.graphql.productservice.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "products")
class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long? = null,

    @Column(name = "name", nullable = false, unique = true)
    val name: String,

    @Column(name = "description", nullable = true)
    val description: String?,

    @Column(name = "price", nullable = false)
    val priceInCents: Int,

    @Column(name = "weight", nullable = false)
    val weight: Int
)
