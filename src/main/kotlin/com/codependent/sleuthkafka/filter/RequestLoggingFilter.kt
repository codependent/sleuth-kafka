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
        val headers = exchange.request.headers
        if (!uri.path.contains("/actuator")) {
            logger.debug("Request URI {} - headers {}", uri, headers)
        }
        return chain.filter(exchange)
                .doOnTerminate {
                    logger.debug("|---> Response - URI {} - statusCode {}", uri, exchange.response.statusCode)
                }
    }
}
