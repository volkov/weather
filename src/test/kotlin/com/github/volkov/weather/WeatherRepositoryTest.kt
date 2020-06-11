package com.github.volkov.weather

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.ZonedDateTime


/**
 * User: serg-v
 * Date: 6/12/20
 * Time: 2:13 AM
 */
@SpringBootTest
class WeatherRepositoryTest(@Autowired val weatherRepository: WeatherRepository) {
    @Test
    fun insert() {
        weatherRepository.save(Weather(1, ZonedDateTime.now(), 1.0, 2.0))
    }
}