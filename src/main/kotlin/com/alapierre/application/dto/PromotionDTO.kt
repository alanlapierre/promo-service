package com.alapierre.application.dto

import com.alapierre.core.entity.Bank
import com.alapierre.core.entity.MediaPayment
import com.alapierre.core.entity.ProductCategory
import io.micronaut.serde.annotation.Serdeable.Deserializable
import io.micronaut.serde.annotation.Serdeable.Serializable
import java.time.LocalDate

@Serializable
@Deserializable
data class PromotionDTO(
    val id: String? = null,
    val mediaPayment: List<MediaPayment> = listOf(),
    val bank: List<Bank> = listOf(),
    val productCategory:  List<ProductCategory> = listOf(),

    val installments: Int? = null,
    val interests: Double? = null,
    val discount: Double? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val active: Boolean = true,
    val creationDate: LocalDate = LocalDate.now(),
    val updateDate: LocalDate = LocalDate.now()
)