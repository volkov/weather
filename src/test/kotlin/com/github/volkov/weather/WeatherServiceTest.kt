package com.github.volkov.weather

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.springframework.kafka.core.KafkaTemplate
import java.time.Duration
import java.time.ZonedDateTime

class WeatherServiceTest {

    @Test
    fun diff() {
        val forecastTimestmap = ZonedDateTime.now()
        val weather1 = createWeather(forecastTimestmap, 1.0, forecastTimestmap)
        val weather2 = createWeather(forecastTimestmap, 2.0, forecastTimestmap.minusMinutes(1))
        val weather3 = createWeather(forecastTimestmap, 2.0, forecastTimestmap.minusMinutes(2))

        val weatherRepository = mock<WeatherRepository> {
            on { list(1) } doReturn listOf(
                weather1,
                weather2,
                weather3,
            )
        }
        val service = WeatherService(
            mock<OpenWeatherClient>(),
            weatherRepository,
            mock<CityRepository>(),
            mock<KafkaTemplate<String, String>>(),
            Duration.ZERO,
        )

        val weatherDiffs = service.getWeatherDiffs(1, null)
        assertThat(weatherDiffs).isEqualTo(
            listOf(
                WeatherWithDiff(
                    weather1,
                    listOf(
                        WeatherDiff(Duration.ofMinutes(-2), 1.0, 0.0),
                    ),
                ),
            ),
        )
    }

    @Test
    fun testSample() {
        val start = ZonedDateTime.now()
        val w1 = createWeather(start, 1.0)
        val w2 = createWeather(start.plusHours(1), 2.0)
        val w3 = createWeather(start.plusHours(2), 3.0)
        val w4 = createWeather(start.plusHours(3), 3.0)
        val w6 = createWeather(start.plusHours(5), 3.0)
        val weatherList = listOf(w1, w2, w3, w4, w6)

        assertThat(weatherList.sample(Duration.ofHours(1))).isEqualTo(weatherList)

        assertThat(weatherList.sample(Duration.ofHours(2))).isEqualTo(listOf(w1, w3, w4, w6))
    }

    private fun createWeather(
        timestamp: ZonedDateTime,
        temperature: Double,
        updated: ZonedDateTime = timestamp,
    ) = Weather(1, timestamp, temperature, 0.0, 0, updated)
}
