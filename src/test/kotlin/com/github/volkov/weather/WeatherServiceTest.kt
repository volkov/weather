package com.github.volkov.weather

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import java.time.Duration
import java.time.ZonedDateTime

class WeatherServiceTest {
    @Test
    fun diff() {
        val weatherRepository = mock(WeatherRepository::class.java)
        val service = WeatherService(mock(OpenWeatherClient::class.java), weatherRepository, mock(CityRepository::class.java))

        val forecastTimestmap = ZonedDateTime.now()
        val weather1 = Weather(1, forecastTimestmap, 1.0, 0.0, forecastTimestmap)
        val weather2 = Weather(1, forecastTimestmap, 2.0, 0.0, forecastTimestmap.minusMinutes(1))
        val weather3 = Weather(1, forecastTimestmap, 2.0, 0.0, forecastTimestmap.minusMinutes(2))
        Mockito.`when`(weatherRepository.list(1)).thenReturn(listOf(
                weather1,
                weather2,
                weather3
        ))

        val weatherDiffs = service.getWeatherDiffs(1, null)
        assertThat(weatherDiffs).isEqualTo(listOf(
                WeatherWithDiff(
                        weather1,
                        listOf(
                                WeatherDiff(Duration.ofMinutes(-2), 1.0, 0.0)
                        )
                )
        ))
    }
}