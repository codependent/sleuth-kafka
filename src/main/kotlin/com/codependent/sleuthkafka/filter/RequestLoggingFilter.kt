package com.codependent.sleuthkafka.filter

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import reactor.core.publisher.switchIfEmpty

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
        return chain.filter(exchange)
                .switchIfEmpty {
                    Mono.subscriberContext()
                            .flatMap {
                                if (!uri.path.contains("/actuator")) {
                                    logger.info("|---> Response - {} {} - statusCode {} - time {}ms",
                                            method, uri, exchange.response.statusCode, (System.currentTimeMillis() - (it.get("processingStartTime") as Long)))
                                }
                                Mono.empty<Void>()
                            }
                }.subscriberContext {
                    it.put("processingStartTime", System.currentTimeMillis())
                }
    }
}
