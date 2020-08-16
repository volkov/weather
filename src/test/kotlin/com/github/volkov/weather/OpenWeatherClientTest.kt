package com.github.volkov.weather

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


/**
 * User: serg-v
 * Date: 8/16/20
 * Time: 12:54 PM
 */
class OpenWeatherClientTest {

    @Test
    fun testConvert() {
        testResult("""{}"""", 0.0)
        testResult("""{"rain": {}}"""", 0.0)
        testResult("""{"rain": {"1h": 1}}"""", 3.0)
        testResult("""{"rain": {"3h": 1}}"""", 1.0)
        testResult("""{"rain": {"3h": 1, "1h": 1}}"""", 1.0)

    }

    private fun testResult(input: String, expected: Double) {
        assertThat(rainValue(ObjectMapper().readValue(input, JsonNode::class.java))).isEqualTo(expected)
    }
}