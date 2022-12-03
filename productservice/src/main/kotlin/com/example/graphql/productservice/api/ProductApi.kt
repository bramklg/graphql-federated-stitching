package com.example.graphql.productservice.api

import com.example.graphql.generated.CreateProductInputDTO
import com.example.graphql.generated.ProductDTO
import com.example.graphql.generated.ProductsMutationApi
import com.example.graphql.generated.ProductsQueryApi
import com.example.graphql.productservice.model.mapper.ProductMapper
import com.example.graphql.productservice.service.ProductService
import graphql.schema.DataFetchingEnvironment
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class ProductApi(
    private val productService: ProductService,
    private val productMapper: ProductMapper
) : ProductsQueryApi, ProductsMutationApi {

    @MutationMapping
    override fun createProduct(@Argument product: CreateProductInputDTO, env: DataFetchingEnvironment): ProductDTO {
        val productEntity = productService.create(productMapper.toEntity(product));
        return productMapper.toDTO(productEntity)
    }

    @QueryMapping
    override fun product(@Argument id: Long, env: DataFetchingEnvironment): ProductDTO? {
        return productService.byId(id)?.let { productMapper.toDTO(it) }
    }

    @QueryMapping
    override fun products(env: DataFetchingEnvironment): List<ProductDTO> {
        return productService.all().map { productMapper.toDTO(it) }
    }

    @QueryMapping
    override fun _productsByIds(@Argument ids: MutableList<Long>, env: DataFetchingEnvironment): List<ProductDTO?> {
        val products = productService.byIds(ids).map { productMapper.toDTO(it) }.associateBy { it.id };
        return ids.map { products[it] }
    }
}
