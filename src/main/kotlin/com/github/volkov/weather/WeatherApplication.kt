package com.github.volkov.weather

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
@EnableScheduling
@RestController
class WeatherApplication(
        val weatherService: WeatherService
) {

    @GetMapping("/{location}")
    fun get(@PathVariable("location") location: Long): Any {
        return weatherService.getWeather(location)
    }

    @GetMapping("/{location}/diffs")
    fun getDiffs(@PathVariable("location") location: Long): Any {
        return weatherService.getWeatherDiffs(location)
    }

}

fun main(args: Array<String>) {
    runApplication<WeatherApplication>(*args)
}
