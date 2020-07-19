package com.github.volkov.weather

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.time.ZonedDateTime

@SpringBootApplication
@EnableScheduling
@RestController
class WeatherApplication(
        val weatherService: WeatherService,
        @Value("\${SECRET:secret}") val secret: String
) {

    @GetMapping("/{location}")
    fun get(@PathVariable("location") location: Long, @RequestParam("forecast") forecast: Boolean?): Any {
        return weatherService.getWeather(location, forecast)
    }

    @GetMapping("/{location}/diffs")
    fun getDiffs(
            @PathVariable("location") location: Long,
            @RequestParam("timestamp", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) timestamp: ZonedDateTime?
    ): Any {
        return weatherService.getWeatherDiffs(location, timestamp)
    }

    @PutMapping("/")
    fun putWeather(@RequestBody weather: Weather, @RequestHeader("secret") secret: String) {
        if (secret != this.secret) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Security...")
        }
        weatherService.save(weather)
    }

}

fun main(args: Array<String>) {
    runApplication<WeatherApplication>(*args)
}
