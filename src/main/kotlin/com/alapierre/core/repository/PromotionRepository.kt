package com.alapierre.core.repository

import com.alapierre.core.entity.Promotion
import java.time.LocalDate

interface PromotionRepository {
    fun save(promotion: Promotion): Promotion
    fun update(promotion: Promotion): Promotion
    fun findById(id: String): Promotion?
    fun findAll(): List<Promotion>
    fun deleteById(id: String)
    fun findByActive(active: Boolean): List<Promotion>
    fun findByActiveAndDateRange(active: Boolean, date: LocalDate): List<Promotion>
}