package com.alapierre.application.mapper

import com.alapierre.application.dto.PromotionDTO
import com.alapierre.application.dto.PromotionValidityDTO
import com.alapierre.core.entity.Promotion
import jakarta.inject.Singleton

@Singleton
class PromotionMapper {

    fun toEntity(dto: PromotionValidityDTO) = Promotion(
        startDate = dto.startDate,
        endDate = dto.endDate,
    )


    fun toEntity(dto: PromotionDTO) = Promotion(
        id = dto.id,
        mediaPayment = dto.mediaPayment,
        bank = dto.bank,
        productCategory = dto.productCategory,
        installments = dto.installments,
        interests = dto.interests,
        discount = dto.discount,
        startDate = dto.startDate,
        endDate = dto.endDate,
        active = dto.active,
        creationDate = dto.creationDate,
        updateDate = dto.updateDate
    )


    fun toDto(entity: Promotion) = PromotionDTO(
        id = entity.id,
        mediaPayment = entity.mediaPayment,
        bank = entity.bank,
        productCategory = entity.productCategory,
        installments = entity.installments,
        interests = entity.interests,
        discount = entity.discount,
        startDate = entity.startDate,
        endDate = entity.endDate,
        active = entity.active,
        creationDate = entity.creationDate,
        updateDate = entity.updateDate
    )
}



