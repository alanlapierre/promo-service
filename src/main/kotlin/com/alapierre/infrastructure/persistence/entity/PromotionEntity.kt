package com.alapierre.infrastructure.persistence.entity

import com.alapierre.core.entity.Bank
import com.alapierre.core.entity.MediaPayment
import com.alapierre.core.entity.ProductCategory
import com.alapierre.core.entity.Promotion
import io.micronaut.data.annotation.GeneratedValue
import io.micronaut.data.annotation.Id
import io.micronaut.data.annotation.MappedEntity
import java.time.LocalDate

@MappedEntity
data class PromotionEntity(
    @field:Id
    @field:GeneratedValue
    val id: String? = null,

    val mediaPayment: List<MediaPayment> = listOf(),
    val bank: List<Bank> = listOf(),
    val productCategory:  List<ProductCategory> = listOf(),

    val installments: Int? = null,
    val interests: Double? = null,
    val discount: Double? = null,
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate = LocalDate.now(),
    val active: Boolean = true,
    val creationDate: LocalDate = LocalDate.now(),
    val updateDate: LocalDate = LocalDate.now()
) {
    companion object {
        fun fromDomain(promotion: Promotion): PromotionEntity {
            return PromotionEntity(
                id = promotion.id,
                mediaPayment = promotion.mediaPayment,
                bank = promotion.bank,
                productCategory = promotion.productCategory,
                installments = promotion.installments,
                interests = promotion.interests,
                discount = promotion.discount,
                startDate = promotion.startDate!!,
                endDate = promotion.endDate!!,
                active = promotion.active,
                creationDate = promotion.creationDate,
                updateDate = promotion.updateDate
            )
        }
    }

    fun toDomain(): Promotion {
        return Promotion(
            id = id,
            mediaPayment = mediaPayment,
            bank = bank,
            productCategory = productCategory,
            installments = installments,
            interests = interests,
            discount = discount,
            startDate = startDate,
            endDate = endDate,
            active = active,
            creationDate = creationDate,
            updateDate = updateDate
        )
    }
}

