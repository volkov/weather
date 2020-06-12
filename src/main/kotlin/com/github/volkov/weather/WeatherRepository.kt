package com.github.volkov.weather

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.Timestamp
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * User: serg-v
 * Date: 6/12/20
 * Time: 2:01 AM
 */
@Repository
class WeatherRepository(val jdbcTemplate: NamedParameterJdbcTemplate) {

    fun save(weather: Weather) {
        jdbcTemplate.update(
                "insert into weather (location_id, timestamp, temperature, rain) values (:locationId, :timestamp, :temperature, :rain)",
                mapOf(
                        "locationId" to weather.locationId,
                        "timestamp" to Timestamp.from(weather.timestamp.toInstant()),
                        "temperature" to weather.temperature,
                        "rain" to weather.rain
                )
        )
    }

    fun list(locationId: Int): List<Weather> {
        return jdbcTemplate.query(
                "select * from weather where location_id = :locationId",
                mapOf("locationId" to locationId)
        ) { rs, _ ->
            Weather(
                    locationId = rs.getLong("location_id"),
                    timestamp = ZonedDateTime.ofInstant(rs.getTimestamp("timestamp").toInstant(), ZoneId.systemDefault()),
                    temperature = rs.getDouble("temperature"),
                    rain = rs.getDouble("rain")
            )
        }
    }

}