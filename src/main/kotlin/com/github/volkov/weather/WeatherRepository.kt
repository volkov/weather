package com.github.volkov.weather

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

/**
 * User: serg-v
 * Date: 6/12/20
 * Time: 2:01 AM
 */
@Repository
class WeatherRepository(val jdbcTemplate: NamedParameterJdbcTemplate) {

    fun save(weather: Weather) {
        jdbcTemplate.update(
                "insert into weather (location_id, timestamp, temperature, rain, updated)" +
                        " values (:locationId, :timestamp, :temperature, :rain, :updated)",
                mapOf(
                        "locationId" to weather.locationId,
                        "timestamp" to weather.timestamp.toTimestamp(),
                        "temperature" to weather.temperature,
                        "rain" to weather.rain,
                        "updated" to weather.updated.toTimestamp()
                )
        )
    }

    fun list(locationId: Long): List<Weather> {
        return jdbcTemplate.query(
                "select * from weather where location_id = :locationId",
                mapOf("locationId" to locationId)
        ) { rs, _ ->
            Weather(
                    locationId = rs.getLong("location_id"),
                    timestamp = rs.getTimestamp("timestamp").toZonedDateTime(),
                    temperature = rs.getDouble("temperature"),
                    rain = rs.getDouble("rain"),
                    updated = rs.getTimestamp("updated").toZonedDateTime()

            )
        }
    }

    fun locations(): List<Long> {
        return jdbcTemplate.queryForList(
                "select distinct(location_id) from weather",
                emptyMap<String, Any>(),
                Long::class.java
        )
    }

}