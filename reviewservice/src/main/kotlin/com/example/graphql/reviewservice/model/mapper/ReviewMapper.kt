package com.example.graphql.reviewservice.model.mapper

import com.example.graphql.generated.CreateReviewInputDTO
import com.example.graphql.generated.ProductDTO
import com.example.graphql.generated.ReviewDTO
import com.example.graphql.generated.UserDTO
import com.example.graphql.reviewservice.model.Review
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingConstants
import org.mapstruct.ReportingPolicy

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING
)
interface ReviewMapper {
    @Mapping(target = "body", source = "review")
    @Mapping(target = "author", source = "authorId")
    @Mapping(target = "product", source = "productId")
    fun toReviewDTO(review: Review): ReviewDTO

    @Mapping(target = "review", source = "body")
    @Mapping(target = "authorId", source = "author")
    @Mapping(target = "productId", source = "product")
    fun toEntity(reviewDTO: CreateReviewInputDTO): Review

    fun toUserDTO(id: Long): UserDTO {
        return UserDTO(id, mutableListOf())
    }

    fun toProductDTO(id: Long): ProductDTO {
        return ProductDTO(id, mutableListOf())
    }
}
