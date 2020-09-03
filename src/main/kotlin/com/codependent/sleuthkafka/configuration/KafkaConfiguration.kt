package com.codependent.sleuthkafka.configuration

import com.codependent.sleuthkafka.api.v1.event.dto.Event
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.messaging.Message
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import reactor.core.publisher.EmitterProcessor
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.function.Function
import java.util.function.Supplier

/**
 * @author José A. Íñigo
 */
@Configuration
class KafkaConfiguration {

    private val logger = LoggerFactory.getLogger(javaClass)

    @Bean
    fun eventEmmiterProcessor(): EmitterProcessor<Message<Event>> {
        return EmitterProcessor.create()
    }

    @Bean
    fun event(): Supplier<Flux<Message<Event>>> {
        return Supplier { eventEmmiterProcessor() }
    }

    @Bean
    fun consumer(): Function<Flux<Event>, Mono<Void>> {
        return Function {
            it.map { event ->
                logger.info("consumer() - event {}", event)
            }.then()
        }
    }

}
