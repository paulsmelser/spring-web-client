package io.github.durioneae.web.client

import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZonedDateTime

data class TestClass(val name: String = "Hi",
                     val number: Int = 12,
                     val double: Double = 12.3,
                     val instant: Instant = Instant.now(),
                     val zonedDateTime: ZonedDateTime = ZonedDateTime.now(),
                     val localDateTime: LocalDateTime = LocalDateTime.now(),
                     val offsetDateTime: OffsetDateTime = OffsetDateTime.now())