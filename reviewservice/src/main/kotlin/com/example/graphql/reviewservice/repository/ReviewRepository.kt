package com.example.graphql.reviewservice.repository

import com.example.graphql.reviewservice.model.Review
import org.springframework.data.repository.ListCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ReviewRepository : ListCrudRepository<Review, Long> {

    fun findAllByProductIdIn(productIds: List<Long>): List<Review>

    fun findAllByAuthorIdIn(authorIds: List<Long>): List<Review>
}
