package com.example.graphql.reviewservice.configuration

import com.example.graphql.reviewservice.model.Review
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.publisher.Sinks
import reactor.util.concurrent.Queues

@Configuration
class AsyncConfiguration {

    /* In memory Sink to demo subscriptions */
    @Bean
    fun reviewSink(): Sinks.Many<Review> {
        return Sinks.many().multicast().onBackpressureBuffer(Queues.SMALL_BUFFER_SIZE, false)
    }
}
