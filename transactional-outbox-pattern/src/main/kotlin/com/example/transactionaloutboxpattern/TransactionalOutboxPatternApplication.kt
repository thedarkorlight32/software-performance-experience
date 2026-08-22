package com.example.transactionaloutboxpattern

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
open class TransactionalOutboxPatternApplication

fun main(args: Array<String>) {
    runApplication<TransactionalOutboxPatternApplication>(*args)
}
