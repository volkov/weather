package com.github.volkov.weather

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.ZonedDateTime

class WeatherServiceITest(
    @Autowired val weatherService: WeatherService,
    @Autowired val weatherConsumer: WeatherConsumer,
) : WeatherApplicationBaseTest() {
    @Test
    fun testSave() {
        assertEquals(0, weatherConsumer.data.size)
        weatherService.save(Weather(123, ZonedDateTime.now(), 0.0, 0.0, 0))
        Thread.sleep(5000)
        assertEquals(1, weatherConsumer.data.size)
    }
}
