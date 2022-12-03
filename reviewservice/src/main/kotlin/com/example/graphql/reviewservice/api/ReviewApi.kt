package com.example.graphql.reviewservice.api

import com.example.graphql.generated.CreateReviewInputDTO
import com.example.graphql.generated.ProductDTO
import com.example.graphql.generated.ReviewDTO
import com.example.graphql.generated.ReviewsMutationApi
import com.example.graphql.generated.ReviewsQueryApi
import com.example.graphql.generated.ReviewsSubscriptionApi
import com.example.graphql.generated.UserDTO
import com.example.graphql.reviewservice.model.Review
import com.example.graphql.reviewservice.model.mapper.ReviewMapper
import com.example.graphql.reviewservice.service.ReviewService
import graphql.schema.DataFetchingEnvironment
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SubscriptionMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks

@Controller
class ReviewApi(
    private val reviewService: ReviewService,
    private val reviewMapper: ReviewMapper,
    private val reviewSink: Sinks.Many<Review>
) : ReviewsQueryApi, ReviewsMutationApi, ReviewsSubscriptionApi {

    @QueryMapping
    override fun review(@Argument id: Long, env: DataFetchingEnvironment): ReviewDTO? {
        return reviewService.byId(id)?.let {
            reviewMapper.toReviewDTO(it)
        }
    }

    @QueryMapping
    override fun reviews(env: DataFetchingEnvironment): List<ReviewDTO> {
        return reviewService.all().map { reviewMapper.toReviewDTO(it) }
    }

    @QueryMapping
    override fun _reviewProductsByProductIds(@Argument ids: List<Long>, env: DataFetchingEnvironment): List<ProductDTO?> {
        val productDTOS = ids.map { reviewMapper.toProductDTO(it) }.associateBy { it.id }

        reviewService.byProductIds(ids).map { reviewMapper.toReviewDTO(it) }.forEach { review ->
            productDTOS[review.product.id]?.let {
                review.product = it
                it.reviews.add(review)
            }

        }

        return ids.map { productDTOS[it] }
    }

    @QueryMapping
    override fun _reviewUsersByUserIds(@Argument ids: MutableList<Long>, env: DataFetchingEnvironment): List<UserDTO?> {
        val userDTOS = ids.map { reviewMapper.toUserDTO(it) }.associateBy { it.id }

        reviewService.byAuthorIds(ids).map { reviewMapper.toReviewDTO(it) }.forEach { review ->
            userDTOS[review.author.id]?.let {
                review.author = it
                it.reviews.add(review)
            }
        }

        return ids.map { userDTOS[it] }
    }

    @MutationMapping
    override fun createReview(@Argument review: CreateReviewInputDTO, env: DataFetchingEnvironment): ReviewDTO {
        val reviewEntity = reviewService.create(reviewMapper.toEntity(review))

        reviewSink.tryEmitNext(reviewEntity)

        return reviewMapper.toReviewDTO(reviewEntity)
    }

    @SubscriptionMapping
    override fun newReviews(env: DataFetchingEnvironment): Flux<ReviewDTO> {
        return reviewSink.asFlux().map { reviewMapper.toReviewDTO(it) }
    }

}
