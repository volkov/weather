package com.github.volkov.weather

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.getForObject

const val BASE_URL = "https://api.openweathermap.org/data/2.5"

@Component
class OpenWeatherClient(@Value("\${OPENWEATHER_TOKEN:}") val token: String) {

    val restTemplate = RestTemplate()

    fun forecast(id: Long): List<Weather> {
        val list = restTemplate.getForObject<JsonNode>(
                "$BASE_URL/forecast?id=$id&units=metric&appid=$token"
        ).get("list")!!.asIterable()
        return list.map { fromJson(id, true, it) }
    }

    private fun fromJson(id: Long, isForecast: Boolean, it: JsonNode): Weather {
        val rain = it["rain"]?.get("3h")?.asDouble() ?: 0.0
        return Weather(
                locationId = id,
                timestamp = toZonedDateTime(it["dt"].intValue()),
                temperature = it["main"].get("temp").doubleValue(),
                rain = rain,
                isForecast = isForecast
        )
    }

    fun current(id: Long): Weather {
        return fromJson(id, false, restTemplate.getForObject("$BASE_URL/weather?id=$id&units=metric&appid=$token"))
    }

}