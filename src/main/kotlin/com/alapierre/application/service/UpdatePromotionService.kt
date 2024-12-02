package com.alapierre.application.service

import com.alapierre.application.exception.PromotionNotFoundException
import com.alapierre.application.service.helpers.DateProvider
import com.alapierre.core.entity.Promotion
import com.alapierre.core.repository.PromotionRepository
import com.alapierre.core.usecase.UpdatePromotionUseCase
import jakarta.inject.Singleton
import java.time.LocalDate


@Singleton
class UpdatePromotionService(private val repository: PromotionRepository,
                             private val validationService: PromotionValidationService,
                             private val dateProvider: DateProvider) : UpdatePromotionUseCase {

    override fun execute(id: String, promotion: Promotion): Promotion {

        val foundPromotion = repository.findById(id) ?: throw PromotionNotFoundException("No se encontró promoción con id $id")

        val promotionToUpdate = foundPromotion.copy(
            id = foundPromotion.id,
            mediaPayment = promotion.mediaPayment,
            bank = promotion.bank,
            productCategory = promotion.productCategory,
            installments = promotion.installments,
            interests = promotion.interests,
            discount = promotion.discount,
            updateDate = dateProvider.now()
        )

        promotionToUpdate.checkAllRules()

        validationService.checkOverlapping(promotionToUpdate, id)
        return repository.update(promotionToUpdate)
    }

}
