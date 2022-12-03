package com.example.graphql.reviewservice.service

import com.example.graphql.reviewservice.model.Review
import com.example.graphql.reviewservice.repository.ReviewRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ReviewService(private val reviewRepository: ReviewRepository) {

    fun byId(id: Long): Review? {
        return reviewRepository.findByIdOrNull(id)
    }

    fun all(): List<Review> {
        return reviewRepository.findAll()
    }

    fun byProductIds(productIds: List<Long>): List<Review> {
        return reviewRepository.findAllByProductIdIn(productIds)
    }

    fun byAuthorIds(authorIds: List<Long>): List<Review> {
        return reviewRepository.findAllByAuthorIdIn(authorIds)
    }

    fun create(review: Review): Review {
        return reviewRepository.save(review)
    }
}
