package com.github.volkov.weather

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import java.time.ZonedDateTime

@Transactional
class WeatherServiceITest(@Autowired val weatherService: WeatherService) : WeatherApplicationBaseTest() {
    @Test
    fun testSave() {
        weatherService.save(Weather(123, ZonedDateTime.now(), 0.0, 0.0, 0))
    }
}
