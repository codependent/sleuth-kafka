package com.codependent.sleuthkafka.filter

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class RequestLoggingFilter : WebFilter {

    private val logger = LoggerFactory.getLogger(javaClass)

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val uri = exchange.request.uri
        if (!uri.path.contains("/actuator")) {
            if (!uri.path.contains("/actuator")) {
                if (logger.isTraceEnabled) {
                    val headers = exchange.request.headers
                    logger.trace("|---> Request - URI {} - headers {}", uri, headers)
                } else if (logger.isDebugEnabled) {
                    logger.debug("|---> Request - URI {}", uri)
                }
            }
        }
        return chain.filter(exchange)
                .doOnTerminate {
                    if (!uri.path.contains("/actuator")) {
                        logger.debug("|---> Response - URI {} - statusCode {}", uri, exchange.response.statusCode)
                    }
                }
    }
}
