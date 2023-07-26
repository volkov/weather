package com.github.volkov.weather

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.stereotype.Repository
import java.util.zip.GZIPInputStream

/**
 * User: serg-v
 * Date: 8/11/20
 * Time: 8:53 PM
 */
@Repository
class CityRepository(@Value("classpath:city.list.json.gz") cityList: Resource) {

    private val data: Map<Long, String>

    init {
        data = GZIPInputStream(cityList.inputStream).use { stream ->
            ObjectMapper().readTree(stream)
                .groupBy { it["id"].longValue() }
                .mapValues { it.value[0]["name"].textValue() }
        }
    }

    fun getName(location: Long): NamedLocation {
        return NamedLocation(location, data.getValue(location))
    }

    fun getNames(locations: List<Long>): List<NamedLocation> {
        return locations.map { getName(it) }
    }
}

data class NamedLocation(val id: Long, val name: String)
