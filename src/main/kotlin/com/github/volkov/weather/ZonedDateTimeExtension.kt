package com.github.volkov.weather

import java.sql.Timestamp
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * User: serg-v
 * Date: 6/12/20
 * Time: 5:04 PM
 */
fun Timestamp.toZonedDateTime(): ZonedDateTime =
        ZonedDateTime.ofInstant(toInstant(), ZoneId.systemDefault())

fun ZonedDateTime.toTimestamp(): Timestamp =
        Timestamp.from(toInstant())

fun Int.toZonedDateTime(): ZonedDateTime =
        ZonedDateTime.ofInstant(
                Instant.ofEpochSecond(toLong()),
                ZoneId.systemDefault()
        )
