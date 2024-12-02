package com.alapierre.api.rest

import com.alapierre.application.dto.PromotionDTO
import com.alapierre.application.dto.PromotionValidityDTO
import com.alapierre.application.mapper.PromotionMapper
import com.alapierre.application.service.CreatePromotionService
import com.alapierre.application.service.DeletePromotionService
import com.alapierre.application.service.PromotionQueryService
import com.alapierre.application.service.UpdatePromotionService
import com.alapierre.core.entity.Bank
import com.alapierre.core.entity.MediaPayment
import com.alapierre.core.entity.ProductCategory
import com.alapierre.core.usecase.UpdatePromotionValidityUseCase
import io.micronaut.http.HttpStatus.*
import io.micronaut.http.annotation.*
import io.micronaut.scheduling.TaskExecutors
import io.micronaut.scheduling.annotation.ExecuteOn
import java.time.LocalDate

@Controller("/promotions")
@ExecuteOn(TaskExecutors.IO)
class PromotionController(
    private val queryService: PromotionQueryService,
    private val createUseCase: CreatePromotionService,
    private val updateUseCase: UpdatePromotionService,
    private val updatePromotionValidityUseCase: UpdatePromotionValidityUseCase,
    private val deleteUseCase: DeletePromotionService,
    private val mapper: PromotionMapper) {

    @Post
    @Status(CREATED)
    fun create(@Body dto: PromotionDTO): PromotionDTO {
        val promotion = mapper.toEntity(dto)
        val createdPromotion = createUseCase.execute(promotion)
        return mapper.toDto(createdPromotion)
    }

    @Put("/{id}")
    @Status(OK)
    fun updateById(@PathVariable id: String, @Body dto: PromotionDTO): PromotionDTO {
        val promotion = mapper.toEntity(dto)
        val updatedPromotion = updateUseCase.execute(id, promotion)
        return mapper.toDto(updatedPromotion)
    }

    @Put("/{id}/validity")
    @Status(OK)
    fun updateValidityById(@PathVariable id: String, @Body dto: PromotionValidityDTO): PromotionDTO {
        val promotion = mapper.toEntity(dto)
        val updatedPromotion = updatePromotionValidityUseCase.execute(id, promotion)
        return mapper.toDto(updatedPromotion)
    }

    @Get("/{id}")
    @Status(OK)
    fun findById(@PathVariable id: String): PromotionDTO {
        val promotion = queryService.findById(id)
        return mapper.toDto(promotion)
    }

    @Get
    @Status(OK)
    fun findAll(): List<PromotionDTO> {
        val promotions = queryService.findAll()
        return promotions.map { mapper.toDto(it) }
    }

    @Get("/active")
    @Status(OK)
    fun findActivePromotions(): List<PromotionDTO> {
        val promotions = queryService.findActive()
        return promotions.map { mapper.toDto(it) }
    }

    @Get("/active/bydate")
    @Status(OK)
    fun findActiveByDatePromotions(@QueryValue date: LocalDate): List<PromotionDTO> {
        val promotions = queryService.findActiveByDate(date)
        return promotions.map { mapper.toDto(it) }
    }

    @Get("/active/bypurchase")
    fun getActiveByPurchasePromotions(
        @QueryValue(defaultValue = "") mediaPayments: List<MediaPayment>?,
        @QueryValue(defaultValue = "") banks: List<Bank>?,
        @QueryValue(defaultValue = "") productCategories: List<ProductCategory>?
    ): List<PromotionDTO> {

        val promotions = queryService.findActiveByPurchase(
            LocalDate.now(),
            mediaPayments ?: listOf(),
            banks ?: listOf(),
            productCategories ?: listOf())

        return promotions.map { mapper.toDto(it) }
    }

    @Delete("/{id}")
    @Status(NO_CONTENT)
    fun deleteById(@PathVariable id: String) =
        deleteUseCase.execute(id)

}