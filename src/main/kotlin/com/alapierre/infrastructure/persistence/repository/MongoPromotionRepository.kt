package com.alapierre.infrastructure.persistence.repository

import com.alapierre.core.entity.Promotion
import com.alapierre.core.repository.PromotionRepository
import com.alapierre.infrastructure.persistence.entity.PromotionEntity
import jakarta.inject.Singleton
import java.time.LocalDate

@Singleton
class MongoPromotionRepository(private val dataRepository: DataPromotionRepository) : PromotionRepository {

    override fun save(promotion: Promotion): Promotion {
        val document = PromotionEntity.fromDomain(promotion)
        val savedDocument = dataRepository.save(document)
        return savedDocument.toDomain()
    }

    override fun update(promotion: Promotion): Promotion {
        val document = PromotionEntity.fromDomain(promotion)
        val savedDocument = dataRepository.update(document)
        return savedDocument.toDomain()
    }

    override fun findById(id: String): Promotion? =
        dataRepository.findById(id).map {it.toDomain()}.orElse(null)

    override fun findAll(): List<Promotion> =
        dataRepository.findAll().map{ it.toDomain() }.toList()

    override fun findByActive(active: Boolean): List<Promotion> =
        dataRepository.findByActive(active).map{ it.toDomain() }.toList()

    override fun findByActiveAndDateRange(active: Boolean, date: LocalDate): List<Promotion> =
        dataRepository.findByActiveAndStartDateLessThanEqualsAndEndDateGreaterThanEquals(active, date, date).map{ it.toDomain() }.toList()

    override fun deleteById(id: String) =
        dataRepository.deleteById(id)

}


