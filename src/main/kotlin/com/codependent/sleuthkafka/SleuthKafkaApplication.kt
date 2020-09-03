package com.codependent.sleuthkafka

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.transaction.annotation.EnableTransactionManagement

/**
 * @author José A. Íñigo
 */
@EnableTransactionManagement
@SpringBootApplication
class SleuthKafkaApplication

fun main(args: Array<String>) {
    //BlockHound.install()
    runApplication<SleuthKafkaApplication>(*args)
}
