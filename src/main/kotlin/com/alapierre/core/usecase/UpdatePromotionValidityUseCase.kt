package com.alapierre.core.usecase

import com.alapierre.core.entity.Promotion

interface UpdatePromotionValidityUseCase {
    fun execute(id: String, promotion: Promotion): Promotion
}