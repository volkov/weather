package com.github.volkov.weather

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.time.Duration
import java.time.ZonedDateTime

@SpringBootApplication
@EnableScheduling
@RestController
class WeatherApplication(
        val weatherService: WeatherService,
        @Value("\${SECRET:secret}") val secret: String
) {

    @GetMapping("api/{location}")
    fun get(@PathVariable("location") location: Long, @RequestParam("forecast") forecast: Boolean?): Any {
        return weatherService.getWeather(location, forecast)
    }

    @GetMapping("api/{location}/diffs")
    fun getDiffs(
            @PathVariable("location") location: Long,
            @RequestParam("from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            from: ZonedDateTime?
    ): Any {
        return weatherService.getWeatherDiffs(location, from ?: ZonedDateTime.now().minusDays(7))
    }

    @GetMapping("api/{location}/forecast")
    fun getForecast(
            @PathVariable("location") location: Long,
            @RequestParam("duration", required = false) duration: Duration?,
            @RequestParam("from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            from: ZonedDateTime?
    ): Any {
        return weatherService.getForecast(location, duration ?: Duration.ZERO, from ?: ZonedDateTime.now().minusDays(7))
    }

    @PutMapping("api/")
    fun putWeather(@RequestBody weather: Weather, @RequestHeader("secret") secret: String) {
        if (secret != this.secret) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Security...")
        }
        weatherService.save(weather)
    }

    @GetMapping("api/locations")
    fun locations(): Any {
        return weatherService.locations()
    }

    @GetMapping("api/location/{location}")
    fun location(@PathVariable("location") location: Long): Any {
        return weatherService.location(location)
    }

}

fun main(args: Array<String>) {
    runApplication<WeatherApplication>(*args)
}
