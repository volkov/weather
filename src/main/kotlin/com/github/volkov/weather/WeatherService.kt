package com.github.volkov.weather

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.round

@Component
class WeatherService(
    val weatherClient: OpenWeatherClient,
    val weatherRepository: WeatherRepository,
    val cityRepository: CityRepository,
    @Value("\${GAP_DURATION:PT3H}") val gapDuration: Duration,
) {

    val logger: Logger = LoggerFactory.getLogger(javaClass)

    @Synchronized
    @Scheduled(cron = "\${WEATHER_UPDATE_CRON:0 * * * * ?}")
    fun update() {
        val locations = weatherRepository.locations()
        if (locations.isEmpty()) {
            logger.info("No locations to update.")
            return
        }
        logger.info("Going to update ${locations.size} locations...")
        for (location in locations) {
            loadAndSave(location)
        }
        logger.info("Updated.")
    }

    fun getWeather(location: Long, forecast: Boolean?): List<Weather> {
        val result = weatherRepository.list(location, forecast = forecast)
        if (result.isNotEmpty()) {
            return result
        }
        return loadAndSave(location)
    }

    fun loadAndSave(location: Long): List<Weather> {
        val forecast = weatherClient.forecast(location).toMutableList()
        forecast.add(weatherClient.current(location))
        forecast.forEach { weatherRepository.save(it) }
        return forecast
    }

    fun getWeatherDiffs(location: Long, from: ZonedDateTime?): List<WeatherWithDiff> {
        return weatherRepository.list(location, from)
            .groupBy { it.timestamp }
            .mapValues { entry ->
                val sortedWeather = entry.value.sortedByDescending { it.updated }
                val latestWeather = sortedWeather[0]
                WeatherWithDiff(
                    weather = latestWeather,
                    diffs = getDiffs(latestWeather, sortedWeather.drop(1)),
                )
            }
            .values.sortedBy { it.weather.timestamp }.toList()
    }

    private fun getDiffs(latestWeather: Weather, sortedWeathers: List<Weather>): List<WeatherDiff> {
        var previous: Weather? = null
        val result = ArrayList<WeatherDiff>()
        for (weather in sortedWeathers.reversed()) {
            if (previous != null && previous.temperature == weather.temperature && previous.rain == weather.rain) {
                continue
            }
            result.add(
                WeatherDiff(
                    timeDelta = truncatedDuration(latestWeather.updated, weather.updated),
                    temperatureDelta = roundTemperature(weather.temperature!! - latestWeather.temperature!!),
                    rainDelta = weather.rain!! - latestWeather.rain!!,
                ),
            )
            previous = weather
        }

        return result.reversed()
    }

    private fun truncatedDuration(from: ZonedDateTime, to: ZonedDateTime) =
        Duration.between(from.truncatedTo(ChronoUnit.MINUTES), to.truncatedTo(ChronoUnit.MINUTES))

    private fun roundTemperature(temperature: Double): Double {
        return round(temperature * 10) / 10
    }

    fun save(weather: Weather) {
        weatherRepository.save(weather)
    }

    fun locations(): List<NamedLocation> {
        return cityRepository.getNames(weatherRepository.locations())
    }

    fun location(location: Long): NamedLocation {
        return cityRepository.getName(location)
    }

    fun getForecast(location: Long, duration: Duration, from: ZonedDateTime): List<Weather> {
        return getForecastData(duration, location, from)
            .sortedBy { it.timestamp }
            .sample(gapDuration)
            .addNulls(gapDuration)
    }

    private fun getForecastData(duration: Duration, location: Long, from: ZonedDateTime): List<Weather> {
        if (duration.isZero) {
            return weatherRepository.list(location, from, false)
        }
        return weatherRepository.list(location, from, true, duration, duration + gapDuration)
            .groupBy { it.timestamp }
            .values.mapNotNull { values ->
                values.sortedByDescending { it.updated }.firstOrNull { it.updated.plus(duration).isBefore(it.timestamp) }
            }
    }
}

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
