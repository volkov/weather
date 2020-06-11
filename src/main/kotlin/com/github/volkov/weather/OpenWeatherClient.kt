package com.github.volkov.weather

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

const val BASE_URL = "https://api.openweathermap.org/data/2.5"

data class Weather(val timestamp: ZonedDateTime, val temperature: Double, val rain: Double)

@Component
class OpenWeatherClient(@Value("\${OPENWEATHER_TOKEN:}") val token: String) {

    val restTemplate = RestTemplate()

    @Suppress("UNCHECKED_CAST")
    fun getForecast(id: Long): List<Weather> {
        val list = restTemplate.getForObject<JsonNode>(
                "${BASE_URL}/forecast?id=$id&units=metric&appid=$token"
        ).get("list")!!.asIterable()
        return list.map {
            val rain = it["rain"]?.get("3h")?.asDouble() ?: 0.0
            Weather(
                    toZonedDateTime(it["dt"].intValue()),
                    it["main"].get("temp").doubleValue(),
                    rain
            )
        }
    }

    private fun toZonedDateTime(timestamp: Int) =
            ZonedDateTime.ofInstant(
                    Instant.ofEpochSecond(timestamp.toLong()),
                    ZoneId.systemDefault()
            )
}