package com.alapierre.core.usecase

import com.alapierre.core.entity.Promotion

interface UpdatePromotionUseCase {
    fun execute(id: String, promotion: Promotion): Promotion
}