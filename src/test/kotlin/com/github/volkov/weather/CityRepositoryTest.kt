package com.github.volkov.weather

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest


/**
 * User: serg-v
 * Date: 8/12/20
 * Time: 8:21 AM
 */
@SpringBootTest
class CityRepositoryTest (@Autowired val repository: CityRepository) {

    @Test
    fun test() {
        assertThat(repository.getName(498817).name).isEqualTo("Saint Petersburg")
    }

}