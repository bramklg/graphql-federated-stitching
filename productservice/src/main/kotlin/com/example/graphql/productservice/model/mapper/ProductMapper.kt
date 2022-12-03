package com.example.graphql.productservice.model.mapper

import com.example.graphql.generated.CreateProductInputDTO
import com.example.graphql.generated.ProductDTO
import com.example.graphql.productservice.model.Product
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingConstants
import org.mapstruct.ReportingPolicy

@Mapper(
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    componentModel = MappingConstants.ComponentModel.SPRING
)
interface ProductMapper {
    companion object {
        const val ENTITY_PRICE = "price"
        const val DTO_PRICE = "priceInCents"
    }

    @Mapping(target = ENTITY_PRICE, source = DTO_PRICE)
    fun toDTO(product: Product): ProductDTO

    @Mapping(target = DTO_PRICE, source = ENTITY_PRICE)
    fun toEntity(createProductInput: CreateProductInputDTO): Product
}
