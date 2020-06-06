package com.github.volkov.weather

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject

@SpringBootApplication
@RestController
class WeatherApplication(
        @Value("\${OPENWEATHER_TOKEN}") val token: String
) {

    @GetMapping("/")
    fun get(): Any {
        return RestTemplate().getForObject("https://samples.openweathermap.org/data/2.5/weather?id=2172797&appid=$token")
    }

}

fun main(args: Array<String>) {
    runApplication<WeatherApplication>(*args)
}
