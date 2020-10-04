package com.codependent.sleuthkafka.filter

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import reactor.core.publisher.SignalType
import reactor.core.publisher.SignalType.*
import reactor.core.publisher.switchIfEmpty
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

@Component
class RequestLoggingFilter : WebFilter {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val method = exchange.request.method
        val uri = exchange.request.uri
        val headers = exchange.request.headers
        if (!uri.path.contains("/actuator")) {
            if (logger.isDebugEnabled) {
                logger.debug("|---> Request - {} {} - headers {}", method, uri, headers)
            } else {
                logger.info("|---> Request - {} {}", method, uri)
            }
        }
        val startTime = AtomicReference<Long>()
        return chain.filter(exchange)
                .doOnSubscribe { startTime.set(System.nanoTime()) }
                .doFinally {
                    if (it == ON_COMPLETE) {
                        if (!uri.path.contains("/actuator")) {
                            logger.info("|---> Response - {} {} - statusCode {} - time {}ms",
                                    method, uri, exchange.response.statusCode, TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime.get()))
                        }
                    }
                }
    }
}
