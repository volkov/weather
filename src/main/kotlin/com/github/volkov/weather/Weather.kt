package com.github.volkov.weather

import java.time.Duration
import java.time.ZonedDateTime

data class Weather(
    val locationId: Long,
    val timestamp: ZonedDateTime,
    val temperature: Double?,
    val rain: Double?,
    val clouds: Int?,
    val updated: ZonedDateTime = ZonedDateTime.now(),
    val isForecast: Boolean = true,
)

data class WeatherDiff(
    val timeDelta: Duration,
    val temperatureDelta: Double,
    val rainDelta: Double,
)

data class WeatherWithDiff(
    val weather: Weather,
    val diffs: List<WeatherDiff>,
)

fun List<Weather>.sample(duration: Duration): List<Weather> {
    val result = ArrayList<Weather>()
    var prev: Weather? = null
    var candidate: Weather? = null
    for (item in this) {
        if (prev == null) {
            result.add(item)
            prev = item
            continue
        }
        if (Duration.between(prev.timestamp, item.timestamp) <= duration) {
            candidate = item
            continue
        }
        if (candidate != null) {
            result.add(candidate)
            prev = candidate
            candidate = null
            if (Duration.between(prev.timestamp, item.timestamp) > duration) {
                result.add(item)
                prev = item
            } else {
                candidate = item
            }
        }
    }
    if (candidate != null) {
        result.add(candidate)
    }
    return result
}

fun List<Weather>.addNulls(duration: Duration): List<Weather> {
    val result = ArrayList<Weather>()
    var prev: Weather? = null
    for (item in this) {
        if (prev != null && Duration.between(prev.timestamp, item.timestamp) > duration) {
            result.add(Weather(item.locationId, prev.timestamp.plus(duration), null, null, null))
        }
        result.add(item)
        prev = item
    }
    return result
}
