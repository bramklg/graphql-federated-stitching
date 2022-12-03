package com.example.graphql.productservice.configuration

import graphql.schema.idl.SchemaPrinter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.execution.GraphQlSource

@Configuration
class GraphQlConfiguration {
    @Bean
    fun graphQlSdl(graphQlSource: GraphQlSource) : String {
        val schemaPrinter = SchemaPrinter()
        return schemaPrinter.print(graphQlSource.schema())
    }
}
