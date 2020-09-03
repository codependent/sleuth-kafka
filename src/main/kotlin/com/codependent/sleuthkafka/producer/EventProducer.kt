package com.codependent.sleuthkafka.producer

import com.codependent.sleuthkafka.api.v1.event.dto.Event
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.kafka.support.KafkaHeaders.MESSAGE_KEY
import org.springframework.kafka.support.KafkaHeaders.TIMESTAMP
import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component
import reactor.core.publisher.EmitterProcessor
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.OffsetDateTime


/**
 * @author José A. Íñigo
 */
@Component
class EventProducer(@Qualifier("eventEmmiterProcessor") private val eventProcessor: EmitterProcessor<Message<Event>>) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun send(event: Event): Mono<Unit> {
        logger.info("send() - event {}", event)
        return sendBlocking(event)
    }

    private fun sendBlocking(event: Event): Mono<Unit> {
        val message = MessageBuilder.withPayload(event)
                .setHeader(MESSAGE_KEY, event.id)
                .setHeader(TIMESTAMP, OffsetDateTime.now().toInstant().toEpochMilli())
                .build()
        eventProcessor.onNext(message)
        return Mono.empty<Unit>()
    }

    private fun sendInCallable(event: Event): Mono<Unit> {
        return Mono.fromCallable {
            val message = MessageBuilder.withPayload(event)
                    .setHeader(MESSAGE_KEY, event.id)
                    .setHeader(TIMESTAMP, OffsetDateTime.now().toInstant().toEpochMilli())
                    .build()
            eventProcessor.onNext(message)
        }.subscribeOn(Schedulers.elastic())
    }

}
