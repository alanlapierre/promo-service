package com.alapierre.infrastructure.persistence.repository

import com.alapierre.infrastructure.persistence.entity.PromotionEntity
import io.micronaut.data.mongodb.annotation.MongoRepository
import io.micronaut.data.repository.CrudRepository
import java.time.LocalDate

@MongoRepository
interface DataPromotionRepository: CrudRepository<PromotionEntity, String> {
    fun findByActive(active: Boolean): List<PromotionEntity>
    fun findByActiveAndStartDateLessThanEqualsAndEndDateGreaterThanEquals(active: Boolean, date1: LocalDate, date2: LocalDate): List<PromotionEntity>
}