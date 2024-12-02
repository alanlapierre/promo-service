package com.alapierre.core.usecase

import com.alapierre.core.entity.Promotion

interface CreatePromotionUseCase {
    fun execute(promotion: Promotion): Promotion
}