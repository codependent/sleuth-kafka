package com.codependent.sleuthkafka.api.v1.event.controller

import com.codependent.sleuthkafka.api.v1.event.dto.Event
import com.codependent.sleuthkafka.producer.EventProducer
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus.CREATED
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

/**
 * @author José A. Íñigo
 */
@RestController
@RequestMapping("/api/v1/events")
class EventRestController(private val eventProducer: EventProducer) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @PostMapping
    @ResponseStatus(CREATED)
    fun registerEvent(@RequestBody event: Event): Mono<Unit> {
        logger.info("registerEvent() - event {}", event)
        return eventProducer.send(event)
    }

}
