package com.example.graphql.reviewservice.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "reviews")
class Review(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    val id: Long? = null,

    @Column(name = "review", nullable = false)
    val review: String,

    @Column(name = "author", nullable = false)
    val authorId: Long,

    @Column(name = "product", nullable = false)
    val productId: Long
)
