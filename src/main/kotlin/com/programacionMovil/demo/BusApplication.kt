package com.programacionMovil.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class BusApplication

fun main(args: Array<String>) {
	println("hola")
	runApplication<BusApplication>(*args)
}
