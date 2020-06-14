package com.github.volkov.weather

import java.time.Duration
import java.time.ZonedDateTime

data class Weather(
        val locationId: Long,
        val timestamp: ZonedDateTime,
        val temperature: Double,
        val rain: Double,
        val updated: ZonedDateTime = ZonedDateTime.now()
)

data class WeatherDiff(
        val timeDelta: Duration,
        val temperatureDelta: Double,
        val rainDelta: Double
)

data class WeatherWithDiff(
        val weather: Weather,
        val diffs: List<WeatherDiff>
)