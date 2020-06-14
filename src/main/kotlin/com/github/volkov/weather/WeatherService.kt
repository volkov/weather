package com.github.volkov.weather

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Duration
import kotlin.math.round

@Component
class WeatherService(val weatherClient: OpenWeatherClient, val weatherRepository: WeatherRepository) {

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

    fun getWeather(location: Long): List<Weather> {
        val result = weatherRepository.list(location)
        if (result.isNotEmpty()) {
            return result
        }
        return loadAndSave(location)
    }

    fun getWeatherDiffs(location: Long): List<WeatherWithDiff> {
        return getWeather(location)
                .groupBy { it.timestamp }
                .mapValues { entry ->
                    val sortedWeather = entry.value.sortedByDescending { it.updated }
                    val latestWeather = sortedWeather[0]
                    WeatherWithDiff(
                            weather = latestWeather,
                            diffs = getDiffs(latestWeather, sortedWeather.drop(1))
                    )
                }
                .values.toList()
    }

    private fun getDiffs(latestWeather: Weather, sortedWeathers: List<Weather>): List<WeatherDiff> {
        var previous: Weather? = null
        val result = ArrayList<WeatherDiff>()
        for (weather in sortedWeathers.reversed()) {
            if (previous != null && previous.temperature == weather.temperature && previous.rain == weather.rain) {
                continue
            }
            result.add(WeatherDiff(
                    timeDelta = Duration.between(latestWeather.updated, weather.updated),
                    temperatureDelta = roundTemperature(weather.temperature - latestWeather.temperature),
                    rainDelta = weather.rain - latestWeather.rain
            ))
            previous = weather
        }

        return result.reversed()
    }

    private fun roundTemperature(temperature: Double): Double {
        return round(temperature * 10) / 10
    }

    @Synchronized
    fun loadAndSave(location: Long): List<Weather> {
        val forecast = weatherClient.getForecast(location)
        forecast.forEach { weatherRepository.save(it) }
        return forecast
    }

}
