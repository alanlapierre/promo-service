package com.alapierre.application.service


import com.alapierre.application.exception.PromotionNotFoundException
import com.alapierre.application.service.helpers.DateProvider
import com.alapierre.core.entity.Promotion
import com.alapierre.core.repository.PromotionRepository
import com.alapierre.core.usecase.UpdatePromotionValidityUseCase
import jakarta.inject.Singleton

@Singleton
class UpdatePromotionValidityService(private val repository: PromotionRepository,
                                     private val validationService: PromotionValidationService,
                                     private val dateProvider: DateProvider) : UpdatePromotionValidityUseCase {

    override fun execute(id: String, promotion: Promotion): Promotion {
        val foundPromotion = repository.findById(id) ?: throw PromotionNotFoundException("No se encontró promoción con id $id")

        val promotionToUpdate = foundPromotion.copy(
            startDate = promotion.startDate,
            endDate = promotion.endDate,
            updateDate = dateProvider.now())

        promotionToUpdate.checkAllRules()

        validationService.checkOverlapping(promotionToUpdate, id)
        return repository.update(promotionToUpdate)
    }

}

