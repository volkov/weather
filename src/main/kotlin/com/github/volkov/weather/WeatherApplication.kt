package com.github.volkov.weather

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
@RestController
class WeatherApplication(
        val client: OpenWeatherClient
) {

    @GetMapping("/")
    fun get(): Any {
        return client.getForecast(498817)
    }

}

fun main(args: Array<String>) {
    runApplication<WeatherApplication>(*args)
}
