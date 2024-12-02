package com.alapierre.application.service

import com.alapierre.core.entity.Promotion
import com.alapierre.core.repository.PromotionRepository
import com.alapierre.core.usecase.CreatePromotionUseCase
import jakarta.inject.Singleton

@Singleton
class CreatePromotionService (private val repository: PromotionRepository,
                              private val validationService: PromotionValidationService): CreatePromotionUseCase{


    override fun execute(promotion: Promotion): Promotion {

        val promotionToCreate = promotion.copy(active = true)
        promotionToCreate.checkAllRules()

        validationService.checkOverlapping(promotionToCreate)
        return repository.save(promotionToCreate)
    }

}