package com.programacionMovil

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BusApplication

fun main(args: Array<String>) {
    runApplication<BusApplication>(*args)
}
