package com.github.volkov.weather

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

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

    @Synchronized
    fun loadAndSave(location: Long): List<Weather> {
        val forecast = weatherClient.getForecast(location)
        forecast.forEach { weatherRepository.save(it) }
        return forecast
    }

}
