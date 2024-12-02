package com.alapierre.application.service

import com.alapierre.core.entity.Bank
import com.alapierre.core.entity.MediaPayment
import com.alapierre.core.entity.ProductCategory
import com.alapierre.core.entity.Promotion
import com.alapierre.core.repository.PromotionRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate


class PromotionValidationServiceTest {

    private val repository: PromotionRepository = mockk()
    private val validationService = PromotionValidationService(repository)

    @Test
    fun `should throw exception if a new promotion overlaps`() {
        // Datos de entrada
        val existingPromotion = Promotion(
            id = "1",
            mediaPayment = listOf(MediaPayment.TARJETA_CREDITO),
            bank = listOf(Bank.GALICIA),
            productCategory = listOf(ProductCategory.TECNOLOGIA),
            startDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 12, 31)
        )

        val newPromotion = Promotion(
            mediaPayment = listOf(MediaPayment.TARJETA_CREDITO),
            bank = listOf(Bank.SANTANDER_RIO),
            productCategory = listOf(ProductCategory.TECNOLOGIA),
            startDate = LocalDate.of(2024, 6, 1),
            endDate = LocalDate.of(2024, 6, 30)
        )

        // Configurar mock
        every { repository.findAll() } returns listOf(existingPromotion)

        // Ejecutar y verificar excepción
        val exception = assertThrows<IllegalArgumentException> {
            validationService.checkOverlapping(newPromotion)
        }
        assert(exception.message == "Ya existe una promoción que se solapa para alguno de los medios de pago, bancos o categorías")

        // Verificar interacciones
        verify { repository.findAll() }
    }

    @Test
    fun `should not throw exception if a new promotion does not overlap`() {
        // Datos de entrada
        val existingPromotion = Promotion(
            id = "1",
            mediaPayment = listOf(MediaPayment.TARJETA_DEBITO),
            bank = listOf(Bank.GALICIA),
            productCategory = listOf(ProductCategory.HOGAR),
            startDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 12, 31)
        )

        val newPromotion = Promotion(
            mediaPayment = listOf(MediaPayment.TARJETA_CREDITO),
            bank = listOf(Bank.SANTANDER_RIO),
            productCategory = listOf(ProductCategory.TECNOLOGIA),
            startDate = LocalDate.of(2025, 1, 1),
            endDate = LocalDate.of(2025, 12, 31)
        )

        // Configurar mock
        every { repository.findAll() } returns listOf(existingPromotion)

        // Ejecutar
        validationService.checkOverlapping(newPromotion)

        // Verificar interacciones
        verify { repository.findAll() }
    }

    @Test
    fun `should throw exception if an updated promotion overlaps`() {
        // Datos de entrada
        val existingPromotion = Promotion(
            id = "1",
            mediaPayment = listOf(MediaPayment.TARJETA_CREDITO),
            bank = listOf(Bank.GALICIA),
            productCategory = listOf(ProductCategory.TECNOLOGIA),
            startDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 12, 31)
        )

        val updatedPromotion = Promotion(
            id = "2",
            mediaPayment = listOf(MediaPayment.TARJETA_CREDITO),
            bank = listOf(Bank.SANTANDER_RIO),
            productCategory = listOf(ProductCategory.TECNOLOGIA),
            startDate = LocalDate.of(2024, 6, 1),
            endDate = LocalDate.of(2024, 6, 30)
        )

        // Configurar mock
        every { repository.findAll() } returns listOf(existingPromotion)

        // Ejecutar y verificar excepción
        val exception = assertThrows<IllegalArgumentException> {
            validationService.checkOverlapping(updatedPromotion, updatedPromotion.id)
        }
        assert(exception.message == "Ya existe una promoción que se solapa para alguno de los medios de pago, bancos o categorías")

        // Verificar interacciones
        verify { repository.findAll() }
    }

    @Test
    fun `should not throw exception if an updated promotion does not overlap`() {
        // Datos de entrada
        val existingPromotion = Promotion(
            id = "1",
            mediaPayment = listOf(MediaPayment.TARJETA_DEBITO),
            bank = listOf(Bank.GALICIA),
            productCategory = listOf(ProductCategory.HOGAR),
            startDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 12, 31)
        )

        val updatedPromotion = Promotion(
            id = "2",
            mediaPayment = listOf(MediaPayment.TARJETA_CREDITO),
            bank = listOf(Bank.SANTANDER_RIO),
            productCategory = listOf(ProductCategory.TECNOLOGIA),
            startDate = LocalDate.of(2025, 1, 1),
            endDate = LocalDate.of(2025, 12, 31)
        )

        // Configurar mock
        every { repository.findAll() } returns listOf(existingPromotion)

        // Ejecutar
        validationService.checkOverlapping(updatedPromotion, updatedPromotion.id)

        // Verificar interacciones
        verify { repository.findAll() }
    }
}
