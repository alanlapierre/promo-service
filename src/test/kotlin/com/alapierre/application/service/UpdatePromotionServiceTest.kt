package com.alapierre.application.service

import com.alapierre.application.exception.PromotionNotFoundException
import com.alapierre.application.service.helpers.DateProvider
import com.alapierre.core.entity.Bank
import com.alapierre.core.entity.MediaPayment
import com.alapierre.core.entity.ProductCategory
import com.alapierre.core.entity.Promotion
import com.alapierre.core.repository.PromotionRepository
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate


class UpdatePromotionServiceTest {

    val dateProvider: DateProvider = mockk()
    private val repository: PromotionRepository = mockk()
    private val validationService: PromotionValidationService = mockk()
    private val updatePromotionService = UpdatePromotionService(repository, validationService, dateProvider)

    @Test
    fun `should update promotion successfully when it exists and is valid`() {
        // Datos de entrada
        val promotionId = "123"

        // Mock del proveedor de fechas
        val fixedDate = LocalDate.of(2024, 12, 2)
        every { dateProvider.now() } returns fixedDate

        val existingPromotion = Promotion(
            id = promotionId,
            mediaPayment = listOf(MediaPayment.TARJETA_CREDITO),
            bank = listOf(Bank.GALICIA),
            productCategory = listOf(ProductCategory.TECNOLOGIA),
            discount = 10.0,
            startDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 12, 31),
            active = true
        )

        val updatedData = Promotion(
            mediaPayment = listOf(MediaPayment.TARJETA_DEBITO),
            bank = listOf(Bank.SANTANDER_RIO),
            productCategory = listOf(ProductCategory.HOGAR),
            discount = 15.0,
            startDate = LocalDate.of(2024, 2, 1),
            endDate = LocalDate.of(2024, 11, 30),
        )

        val updatedPromotion = existingPromotion.copy(
            mediaPayment = updatedData.mediaPayment,
            bank = updatedData.bank,
            productCategory = updatedData.productCategory,
            discount = updatedData.discount,
            updateDate = fixedDate
        )

        // Configurar mocks
        every { repository.findById(promotionId) } returns existingPromotion
        every { validationService.checkOverlapping(updatedPromotion, promotionId) } just Runs
        every { repository.update(updatedPromotion) } returns updatedPromotion

        // Ejecutar
        val result = updatePromotionService.execute(promotionId, updatedData)

        // Verificar
        assertEquals(updatedPromotion, result)
        verify { repository.findById(promotionId) }
        verify { validationService.checkOverlapping(updatedPromotion, promotionId) }
        verify { repository.update(updatedPromotion) }
    }

    @Test
    fun `should throw exception if promotion does not exist`() {
        // Datos de entrada
        val promotionId = "123"

        val updatedData = Promotion(
            mediaPayment = listOf(MediaPayment.TARJETA_DEBITO),
            bank = listOf(Bank.SANTANDER_RIO),
            productCategory = listOf(ProductCategory.HOGAR),
            discount = 15.0
        )

        // Configurar mock
        every { repository.findById(promotionId) } returns null

        // Ejecutar y verificar excepción
        val exception = assertThrows<PromotionNotFoundException> {
            updatePromotionService.execute(promotionId, updatedData)
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
            mediaPayment = listOf(MediaPayment.TARJETA_CREDITO),
            bank = listOf(Bank.GALICIA),
            productCategory = listOf(ProductCategory.TECNOLOGIA),
            startDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 12, 31),
            active = true
        )

        val updatedData = Promotion(
            mediaPayment = listOf(MediaPayment.TARJETA_DEBITO),
            bank = listOf(Bank.SANTANDER_RIO),
            productCategory = listOf(ProductCategory.HOGAR),
            discount = 90.0, // Descuento inválido
            updateDate = fixedDate
        )

        // Configurar mocks
        every { repository.findById(promotionId) } returns existingPromotion

        // Ejecutar y verificar excepción
        val exception = assertThrows<IllegalArgumentException> {
            updatePromotionService.execute(promotionId, updatedData)
        }
        assertEquals("El descuento no puede ser menor a 5% ni mayor a 80%", exception.message)

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
            mediaPayment = listOf(MediaPayment.TARJETA_CREDITO),
            bank = listOf(Bank.GALICIA),
            productCategory = listOf(ProductCategory.TECNOLOGIA),
            startDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 12, 31),
            active = true
        )

        val updatedData = Promotion(
            mediaPayment = listOf(MediaPayment.TARJETA_DEBITO),
            bank = listOf(Bank.SANTANDER_RIO),
            productCategory = listOf(ProductCategory.HOGAR),
            discount = 15.0,
        )

        val updatedPromotion = existingPromotion.copy(
            mediaPayment = updatedData.mediaPayment,
            bank = updatedData.bank,
            productCategory = updatedData.productCategory,
            discount = updatedData.discount,
            updateDate = fixedDate
        )

        // Configurar mocks
        every { repository.findById(promotionId) } returns existingPromotion
        every { validationService.checkOverlapping(updatedPromotion, promotionId) } throws IllegalArgumentException(
            "Ya existe una promoción que se solapa para alguno de los medios de pago, bancos o categorías"
        )

        // Ejecutar y verificar excepción
        val exception = assertThrows<IllegalArgumentException> {
            updatePromotionService.execute(promotionId, updatedData)
        }
        assertEquals("Ya existe una promoción que se solapa para alguno de los medios de pago, bancos o categorías", exception.message)

        // Verificar interacciones
        verify { repository.findById(promotionId) }
        verify { validationService.checkOverlapping(updatedPromotion, promotionId) }
        verify(exactly = 0) { repository.update(any()) }
    }
}
