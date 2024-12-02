package com.alapierre.application.service

import com.alapierre.application.exception.PromotionNotFoundException
import com.alapierre.core.entity.Bank
import com.alapierre.core.entity.MediaPayment
import com.alapierre.core.entity.ProductCategory
import com.alapierre.core.entity.Promotion
import com.alapierre.core.repository.PromotionRepository
import jakarta.inject.Singleton
import java.time.LocalDate

@Singleton
class PromotionQueryService(private val repository: PromotionRepository) {

    fun findById(id: String) : Promotion =
        repository.findById(id) ?: throw PromotionNotFoundException("No se encontró una promoción con el ID $id")

    fun findAll():List<Promotion> =
        repository.findAll()

    fun findActive() : List<Promotion> =
        repository.findByActive(true)

    fun findActiveByDate(date: LocalDate): List<Promotion> =
        repository.findByActiveAndDateRange(true, date)

    fun findActiveByPurchase(date: LocalDate,
                             mediaPayments: List<MediaPayment>,
                             banks: List<Bank>,
                             productCategories: List<ProductCategory>): List<Promotion> {

        if (mediaPayments.isEmpty() && banks.isEmpty() && productCategories.isEmpty()) {
            throw IllegalArgumentException("Debe proporcionarse al menos un criterio de búsqueda")
        }

        val promotions = repository.findByActiveAndDateRange(true, date)

        return  promotions.filter { promotion ->
            promotion.mediaPayment.any { it in mediaPayments } ||
            promotion.bank.any { it in banks } ||
            promotion.productCategory.any { it in productCategories }
        }

    }

}


