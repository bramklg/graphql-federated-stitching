package com.example.graphql.userservice.api

import com.example.graphql.generated.SystemQueryApi
import graphql.schema.DataFetchingEnvironment
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class SystemApi(private val graphQlSdl: String) : SystemQueryApi {

    @QueryMapping
    override fun _sdl(env: DataFetchingEnvironment): String {
        return this.graphQlSdl
    }
}
