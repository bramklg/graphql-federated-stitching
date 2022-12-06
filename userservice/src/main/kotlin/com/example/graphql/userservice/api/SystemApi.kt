package com.example.graphql.userservice.api

import com.example.graphql.generated.SystemQueryApi
import graphql.schema.DataFetchingEnvironment
import org.springframework.stereotype.Controller
import java.util.function.Supplier

@Controller
class SystemApi(private val graphQLSDLSupplier: Supplier<String>) : SystemQueryApi {

    override fun _sdl(env: DataFetchingEnvironment): String {
        return this.graphQLSDLSupplier.get()
    }
}
