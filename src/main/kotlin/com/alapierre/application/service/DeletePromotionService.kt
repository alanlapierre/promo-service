package com.alapierre.application.service

import com.alapierre.core.repository.PromotionRepository
import com.alapierre.core.usecase.DeletePromotionUseCase
import jakarta.inject.Singleton

@Singleton
class DeletePromotionService (private val repository: PromotionRepository,
                              private val queryService: PromotionQueryService): DeletePromotionUseCase {


    override fun execute(id: String) {
        queryService.findById(id)
        repository.deleteById(id)
    }

}