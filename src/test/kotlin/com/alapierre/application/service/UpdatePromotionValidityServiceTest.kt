package com.alapierre.application.service

import com.alapierre.application.exception.PromotionNotFoundException
import com.alapierre.application.service.helpers.DateProvider
import com.alapierre.core.entity.Promotion
import com.alapierre.core.repository.PromotionRepository
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

class UpdatePromotionValidityServiceTest {

    private val repository: PromotionRepository = mockk()
    private val validationService: PromotionValidationService = mockk()
    private val dateProvider: DateProvider = mockk()
    private val updatePromotionValidityService = UpdatePromotionValidityService(repository, validationService, dateProvider)

    @Test
    fun `should update promotion validity successfully`() {
        // Datos de entrada
        val promotionId = "123"

        // Mock del proveedor de fechas
        val fixedDate = LocalDate.of(2024, 12, 2)
        every { dateProvider.now() } returns fixedDate

        val existingPromotion = Promotion(
            id = promotionId,
            startDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 12, 31),
            discount = 20.0,
            active = true
        )

        val newValidity = Promotion(
            startDate = LocalDate.of(2024, 2, 1),
            endDate = LocalDate.of(2024, 11, 30)
        )

        val updatedPromotion = existingPromotion.copy(
            startDate = newValidity.startDate,
            endDate = newValidity.endDate,
            updateDate = fixedDate
        )

        // Configurar mocks
        every { dateProvider.now() } returns fixedDate
        every { repository.findById(promotionId) } returns existingPromotion
        every { validationService.checkOverlapping(updatedPromotion, promotionId) } just Runs
        every { repository.update(updatedPromotion) } returns updatedPromotion

        // Ejecutar
        val result = updatePromotionValidityService.execute(promotionId, newValidity)

        // Verificar
        assertEquals(updatedPromotion, result)
        verify { repository.findById(promotionId) }
        verify { validationService.checkOverlapping(updatedPromotion, promotionId) }
        verify { repository.update(updatedPromotion) }
    }

    @Test
    fun `should throw exception if promotion not found`() {
        // Datos de entrada
        val promotionId = "123"
        val newValidity = Promotion(
            startDate = LocalDate.of(2024, 2, 1),
            endDate = LocalDate.of(2024, 11, 30)
        )

        // Configurar mocks
        every { repository.findById(promotionId) } returns null

        // Ejecutar y verificar excepción
        val exception = assertThrows<PromotionNotFoundException> {
            updatePromotionValidityService.execute(promotionId, newValidity)
        }
        assertEquals("No se encontró promoción con id $promotionId", exception.message)

        // Verificar interacciones
        verify { repository.findById(promotionId) }
        verify(exactly = 0) { validationService.checkOverlapping(any(), any()) }
        verify(exactly = 0) { repository.update(any()) }
    }

    @Test
    fun `should throw exception if updated promotion fails validation`() {
        // Datos de entrada
        val promotionId = "123"

        // Mock del proveedor de fechas
        val fixedDate = LocalDate.of(2024, 12, 2)
        every { dateProvider.now() } returns fixedDate


        val existingPromotion = Promotion(
            id = promotionId,
            startDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 12, 31),
            discount = 20.0,
            active = true
        )

        val invalidValidity = Promotion(
            startDate = LocalDate.of(2024, 2, 1),
            endDate = LocalDate.of(2023, 12, 31) // Fecha inválida
        )

        // Configurar mocks
        every { dateProvider.now() } returns fixedDate
        every { repository.findById(promotionId) } returns existingPromotion

        // Ejecutar y verificar excepción
        val exception = assertThrows<IllegalArgumentException> {
            updatePromotionValidityService.execute(promotionId, invalidValidity)
        }
        assertEquals("La fecha de fin no puede ser anterior a la fecha de inicio", exception.message)

        // Verificar interacciones
        verify { repository.findById(promotionId) }
        verify(exactly = 0) { validationService.checkOverlapping(any(), any()) }
        verify(exactly = 0) { repository.update(any()) }
    }

    @Test
    fun `should throw exception if updated promotion overlaps`() {
        // Datos de entrada
        val promotionId = "123"

        // Mock del proveedor de fechas
        val fixedDate = LocalDate.of(2024, 12, 2)
        every { dateProvider.now() } returns fixedDate


        val existingPromotion = Promotion(
            id = promotionId,
            startDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 12, 31),
            discount = 20.0,
            active = true
        )

        val newValidity = Promotion(
            startDate = LocalDate.of(2024, 2, 1),
            endDate = LocalDate.of(2024, 11, 30)
        )

        val updatedPromotion = existingPromotion.copy(
            startDate = newValidity.startDate,
            endDate = newValidity.endDate,
            updateDate = fixedDate
        )

        // Configurar mocks
        every { dateProvider.now() } returns fixedDate
        every { repository.findById(promotionId) } returns existingPromotion
        every { validationService.checkOverlapping(updatedPromotion, promotionId) } throws IllegalArgumentException(
            "Ya existe una promoción que se solapa para alguno de los medios de pago, bancos o categorías"
        )

        // Ejecutar y verificar excepción
        val exception = assertThrows<IllegalArgumentException> {
            updatePromotionValidityService.execute(promotionId, newValidity)
        }
        assertEquals("Ya existe una promoción que se solapa para alguno de los medios de pago, bancos o categorías", exception.message)

        // Verificar interacciones
        verify { repository.findById(promotionId) }
        verify { validationService.checkOverlapping(updatedPromotion, promotionId) }
        verify(exactly = 0) { repository.update(any()) }
    }
}

