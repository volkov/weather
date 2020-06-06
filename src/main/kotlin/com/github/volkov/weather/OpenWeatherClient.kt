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

data class Weather(val timestamp: ZonedDateTime, val temperature: Int)

@Component
class OpenWeatherClient(@Value("\${OPENWEATHER_TOKEN:}") val token: String) {

    val restTemplate = RestTemplate()

    fun getForecast(id: Long): List<Weather> {
        val list = restTemplate.getForObject<Map<String, List<Map<String, Any>>>>(
                "${BASE_URL}/forecast?id=$id&units=metric&appid=$token", JsonNode::class
        ).getValue("list")
        return list.map { t ->
            val timestamp = t["dt"] as Int
            Weather(
                    toZonedDateTime(timestamp),
                    (t["main"] as Map<String, Int>).getValue("temp")
            )
        }
    }

    private fun toZonedDateTime(timestamp: Int) =
            ZonedDateTime.ofInstant(
                    Instant.ofEpochSecond(timestamp.toLong()),
                    ZoneId.systemDefault()
            )
}