package com.github.volkov.weather

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class WeatherService(val weatherClient: OpenWeatherClient) {

    val logger: Logger = LoggerFactory.getLogger(javaClass)

    val data = mutableMapOf<Long, MutableMap<Instant, MutableMap<Instant, Weather>>>()

    @Synchronized
    @Scheduled(cron = "0 * * * * ?")
    fun update() {
        val timestamp = Instant.now()
        logger.info("Going to update ${data.size} locations")
        for (location in data.keys) {
            loadAndSave(location, timestamp)
        }
        logger.info("Updated.")
    }

    fun getWeather(location: Long): Map<Instant, Map<Instant, Weather>> {
        if (location !in data) {
            loadAndSave(location, Instant.now())
        }
        return data.getValue(location)
    }

    @Synchronized
    fun loadAndSave(location: Long, timestamp: Instant) {
        val forecast = weatherClient.getForecast(location)
        val locationData = data.getOrPut(location, { mutableMapOf() })
        for (weather in forecast) {
            val timedData = locationData.getOrPut(weather.timestamp.toInstant(), { mutableMapOf() })
            timedData[timestamp] = weather
        }
    }

    fun get(location: Long) = data[location]

}
