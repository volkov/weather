package com.github.volkov.weather

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime


/**
 * User: serg-v
 * Date: 6/12/20
 * Time: 2:13 AM
 */
@SpringBootTest
@Transactional
class WeatherRepositoryTest(@Autowired val weatherRepository: WeatherRepository) {

    @Test
    fun insert() {
        saveWeather()
    }

    @Test
    fun select() {
        saveWeather()
        assertThat(weatherRepository.list(1).size).isEqualTo(1)
    }

    @Test
    internal fun locations() {
        saveWeather()
        assertThat(weatherRepository.locations()).isEqualTo(listOf(1L))
    }

    private fun saveWeather() {
        weatherRepository.save(Weather(1, ZonedDateTime.now(), 1.0, 2.0))
    }


}