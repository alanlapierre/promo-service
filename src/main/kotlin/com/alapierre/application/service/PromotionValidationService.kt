package com.alapierre.application.service

import com.alapierre.core.entity.Promotion
import com.alapierre.core.repository.PromotionRepository
import jakarta.inject.Singleton


@Singleton
class PromotionValidationService(private val repository: PromotionRepository) {

    fun checkOverlapping(promotion: Promotion, promotionId: String ? = null) {

        if(promotionId != null) {
            if(isPromotionOverlapping(promotion, promotionId)) {
                throw IllegalArgumentException(
                    "Ya existe una promoción que se solapa para alguno de los medios de pago, bancos o categorías")
            }
        } else {
            if(isPromotionOverlapping(promotion)) {
                throw IllegalArgumentException(
                    "Ya existe una promoción que se solapa para alguno de los medios de pago, bancos o categorías"
                )
            }
        }
    }

    private fun isPromotionOverlapping(promotion: Promotion, promotionId: String ? = null): Boolean {
        val overlappingPromotions = repository.findAll().filter { existingPromotion ->

            // Excluir la promoción que estás actualizando (si tiene un ID)
            if (promotionId != null && existingPromotion.id == promotionId) {
                return@filter false
            }

            // Comparar las reglas de solapamiento
            (existingPromotion.mediaPayment.any { it in promotion.mediaPayment } ||
            existingPromotion.bank.any { it in promotion.bank } ||
            existingPromotion.productCategory.any { it in promotion.productCategory }) &&
            !(promotion.startDate!!.isAfter(existingPromotion.endDate) ||
            promotion.endDate!!.isBefore(existingPromotion.startDate))
        }

        return overlappingPromotions.isNotEmpty()
    }

}