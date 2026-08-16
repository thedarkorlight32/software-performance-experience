package com.example.bigtablejoinperformancetest

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class BigTableJoinPerformanceTestApplication

fun main(args: Array<String>) {
    runApplication<BigTableJoinPerformanceTestApplication>(*args)
}
