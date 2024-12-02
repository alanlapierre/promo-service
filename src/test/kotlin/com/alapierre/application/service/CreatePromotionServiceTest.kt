package com.alapierre.application.service

import com.alapierre.core.entity.Promotion
import com.alapierre.core.repository.PromotionRepository
import io.mockk.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

class CreatePromotionServiceTest {

    private val repository: PromotionRepository = mockk()
    private val validationService: PromotionValidationService = mockk()
    private val createPromotionService = CreatePromotionService(repository, validationService)

    @Test
    fun `should create promotion successfully when all validations pass`() {
        // Datos de entrada
        val promotion = Promotion(
            id = null,
            discount = 10.0,
            startDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 12, 31),
            active = false // No importa porque se copia como true en el servicio
        )
        val promotionToCreate = promotion.copy(active = true)

        // Configurar mocks
        every { validationService.checkOverlapping(promotionToCreate) } just Runs
        every { repository.save(promotionToCreate) } returns promotionToCreate


        // Ejecutar el servicio
        val result = createPromotionService.execute(promotion)

        // Verificar resultados
        assertDoesNotThrow { promotion.checkAllRules() }
        verify { repository.save(promotionToCreate) }
        verify { validationService.checkOverlapping(promotionToCreate) }

        // Validar el resultado
        assert(result == promotionToCreate)
    }

    @Test
    fun `should throw exception when promotion validation fails`() {
        // Datos de entrada con descuento fuera de rango
        val invalidPromotion = Promotion(
            id = null,
            discount = 90.0,
            startDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 12, 31)
        )

        // Ejecutar y verificar que lanza la excepción
        val exception = assertThrows<IllegalArgumentException> {
            createPromotionService.execute(invalidPromotion)
        }
        assert(exception.message == "El descuento no puede ser menor a 5% ni mayor a 80%")

        // Verificar que no se interactúa con los mocks
        verify(exactly = 0) { repository.save(any()) }
        verify(exactly = 0) { validationService.checkOverlapping(any()) }
    }

    @Test
    fun `should throw exception when validationService detects overlapping promotions`() {
        // Datos de entrada
        val promotion = Promotion(
            id = null,
            discount = 10.0,
            startDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 12, 31),
            active = false
        )
        val promotionToCreate = promotion.copy(active = true)

        // Configurar mocks
        every { validationService.checkOverlapping(promotionToCreate) } throws IllegalArgumentException("La promoción se superpone con otra existente")

        // Ejecutar y verificar que lanza la excepción
        val exception = assertThrows<IllegalArgumentException> {
            createPromotionService.execute(promotion)
        }
        assert(exception.message == "La promoción se superpone con otra existente")

        // Verificar interacciones con los mocks
        verify { validationService.checkOverlapping(promotionToCreate) }
        verify(exactly = 0) { repository.save(any()) }
    }

}