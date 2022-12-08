package com.example.graphql.userservice.configuration

import graphql.kickstart.servlet.apollo.ApolloScalars
import graphql.schema.GraphQLScalarType
import graphql.schema.GraphQLSchema
import graphql.schema.idl.SchemaPrinter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import java.util.concurrent.atomic.AtomicReference
import java.util.function.Supplier


@Configuration
class GraphQlConfiguration {
    private val sdlContainer = AtomicReference<String>()

    @Bean
    fun graphQLSDLSupplier(@Lazy graphQLSchema: GraphQLSchema): Supplier<String> {
        // Use a supplier because we have to get the GraphQLSchema injected by proxy because of a circular dependency, this way we only print the schema to a string once
        return Supplier {
            if (sdlContainer.get() == null) {
                sdlContainer.compareAndSet(null, SchemaPrinter().print(graphQLSchema))
            }
            sdlContainer.get()
        }
    }

    @Bean
    fun uploadScalarDefine(): GraphQLScalarType {
        return ApolloScalars.Upload
    }
}
