package com.github.volkov.weather

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

const val TOPIC = "weather"

@Component
class WeatherConsumer {

    val data: MutableList<String> = mutableListOf()

    @KafkaListener(topics = [TOPIC], groupId = "weather.consumer")
    fun consume(message: String) {
        println("WeatherConsumer.consume $message")
        data.add(message)
    }
}
