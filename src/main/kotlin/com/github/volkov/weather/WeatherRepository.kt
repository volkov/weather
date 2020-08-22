package com.github.volkov.weather

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import java.time.Duration
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
                "insert into weather (location_id, timestamp, temperature, rain, updated, forecast)" +
                        " values (:locationId, :timestamp, :temperature, :rain, :updated, :forecast)",
                mapOf(
                        "locationId" to weather.locationId,
                        "timestamp" to weather.timestamp.toTimestamp(),
                        "temperature" to weather.temperature,
                        "rain" to weather.rain,
                        "updated" to weather.updated.toTimestamp(),
                        "forecast" to weather.isForecast
                )
        )
    }

    fun list(
            locationId: Long,
            from: ZonedDateTime? = null,
            forecast: Boolean? = null,
            minDiff: Duration? = null,
            maxDiff: Duration? = null
    ): List<Weather> {
        var query = "select * from weather where location_id = :locationId"
        val params = mutableMapOf<String, Any>("locationId" to locationId)
        if (from != null) {
            query += " and timestamp >= :timestamp"
            params["timestamp"] = from.toTimestamp()
        }
        if (forecast != null) {
            query += " and forecast = :forecast"
            params["forecast"] = forecast
        }
        if (minDiff != null) {
            query += " and timestamp - updated > :minDiff::interval"
            params["minDiff"] = minDiff.toString()
        }
        if (maxDiff != null) {
            query += " and timestamp - updated < :maxDiff::interval"
            params["maxDiff"] = maxDiff.toString()
        }


        return jdbcTemplate.query(
                query,
                params
        ) { rs, _ ->
            Weather(
                    locationId = rs.getLong("location_id"),
                    timestamp = rs.getTimestamp("timestamp").toZonedDateTime(),
                    temperature = rs.getDouble("temperature"),
                    rain = rs.getDouble("rain"),
                    updated = rs.getTimestamp("updated").toZonedDateTime(),
                    isForecast = rs.getBoolean("forecast")

            )
        }
    }

    fun locations(): List<Long> {
        return jdbcTemplate.queryForList(
                "select distinct(location_id) from weather where location_id > 0",
                emptyMap<String, Any>(),
                Long::class.java
        )
    }

}