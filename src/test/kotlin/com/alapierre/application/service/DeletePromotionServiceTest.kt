package com.alapierre.application.service

import com.alapierre.core.entity.Promotion
import com.alapierre.core.repository.PromotionRepository
import io.mockk.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

class DeletePromotionServiceTest {

    private val repository: PromotionRepository = mockk()
    private val queryService: PromotionQueryService = mockk()
    private val deletePromotionService = DeletePromotionService(repository, queryService)

    @Test
    fun `should delete promotion successfully when it exists`() {
        // Datos de entrada
        val promotionId = "123"

        // Configurar mocks
        every { queryService.findById(promotionId) } returns Promotion(
            id = promotionId,
            discount = 10.0,
            startDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 12, 31),
            active = true
        )
        every { repository.deleteById(promotionId) } just Runs

        // Ejecutar el servicio
        deletePromotionService.execute(promotionId)

        // Verificar interacciones
        verify { queryService.findById(promotionId) }
        verify { repository.deleteById(promotionId) }
    }

    @Test
    fun `should throw exception when promotion does not exist`() {
        // Datos de entrada
        val promotionId = "123"

        // Configurar mocks
        every { queryService.findById(promotionId) } throws IllegalArgumentException("Promotion not found")

        // Ejecutar y verificar que lanza excepción
        val exception = assertThrows<IllegalArgumentException> {
            deletePromotionService.execute(promotionId)
        }
        assert(exception.message == "Promotion not found")

        // Verificar interacciones
        verify { queryService.findById(promotionId) }
        verify(exactly = 0) { repository.deleteById(any()) }
    }
}