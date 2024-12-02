package com.alapierre.application.dto

import io.micronaut.serde.annotation.Serdeable.Deserializable
import io.micronaut.serde.annotation.Serdeable.Serializable
import java.time.LocalDate


@Serializable
@Deserializable
data class PromotionValidityDTO(
    val startDate: LocalDate,
    val endDate: LocalDate
)