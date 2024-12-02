package com.alapierre.application.service

import com.alapierre.application.exception.PromotionNotFoundException
import com.alapierre.core.entity.MediaPayment
import com.alapierre.core.entity.Promotion
import com.alapierre.core.repository.PromotionRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate


class PromotionQueryServiceTest {

    private val repository: PromotionRepository = mockk()
    private val queryService = PromotionQueryService(repository)

    @Test
    fun `should return promotion by id when it exists`() {
        // Datos de entrada
        val promotionId = "123"
        val expectedPromotion = Promotion(
            id = promotionId,
            discount = 10.0,
            startDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 12, 31),
            active = true
        )

        // Configurar mock
        every { repository.findById(promotionId) } returns expectedPromotion

        // Ejecutar
        val result = queryService.findById(promotionId)

        // Verificar
        assertEquals(expectedPromotion, result)
        verify { repository.findById(promotionId) }
    }

    @Test
    fun `should throw exception when promotion does not exist`() {
        // Datos de entrada
        val promotionId = "123"

        // Configurar mock
        every { repository.findById(promotionId) } returns null

        // Ejecutar y verificar excepción
        val exception = assertThrows<PromotionNotFoundException> {
            queryService.findById(promotionId)
        }
        assertEquals("No se encontró una promoción con el ID $promotionId", exception.message)
        verify { repository.findById(promotionId) }
    }

    @Test
    fun `should return all promotions`() {
        // Datos de entrada
        val promotions = listOf(
            Promotion(id = "1", discount = 10.0, startDate = LocalDate.of(2024, 1, 1), endDate = LocalDate.of(2024, 12, 31), active = true),
            Promotion(id = "2", discount = 15.0, startDate = LocalDate.of(2024, 2, 1), endDate = LocalDate.of(2024, 3, 31), active = false)
        )

        // Configurar mock
        every { repository.findAll() } returns promotions

        // Ejecutar
        val result = queryService.findAll()

        // Verificar
        assertEquals(promotions, result)
        verify { repository.findAll() }
    }

    @Test
    fun `should return active promotions`() {
        // Datos de entrada
        val activePromotions = listOf(
            Promotion(id = "1", discount = 10.0, startDate = LocalDate.of(2024, 1, 1), endDate = LocalDate.of(2024, 12, 31), active = true)
        )

        // Configurar mock
        every { repository.findByActive(true) } returns activePromotions

        // Ejecutar
        val result = queryService.findActive()

        // Verificar
        assertEquals(activePromotions, result)
        verify { repository.findByActive(true) }
    }

    @Test
    fun `should return active promotions by date`() {
        // Datos de entrada
        val date = LocalDate.of(2024, 6, 15)
        val activePromotions = listOf(
            Promotion(id = "1", discount = 10.0, startDate = LocalDate.of(2024, 1, 1), endDate = LocalDate.of(2024, 12, 31), active = true)
        )

        // Configurar mock
        every { repository.findByActiveAndDateRange(true, date) } returns activePromotions

        // Ejecutar
        val result = queryService.findActiveByDate(date)

        // Verificar
        assertEquals(activePromotions, result)
        verify { repository.findByActiveAndDateRange(true, date) }
    }

    @Test
    fun `should throw exception when no criteria provided for active promotions by purchase`() {
        // Datos de entrada
        val date = LocalDate.of(2024, 6, 15)

        // Ejecutar y verificar excepción
        val exception = assertThrows<IllegalArgumentException> {
            queryService.findActiveByPurchase(date, emptyList(), emptyList(), emptyList())
        }
        assertEquals("Debe proporcionarse al menos un criterio de búsqueda", exception.message)
    }

    @Test
    fun `should return promotions matching purchase criteria`() {
        // Datos de entrada
        val date = LocalDate.of(2024, 6, 15)

        val activePromotions = listOf(
            Promotion(
                id = "1",
                discount = 10.0,
                startDate = LocalDate.of(2024, 1, 1),
                endDate = LocalDate.of(2024, 12, 31),
                active = true,
                mediaPayment = listOf(MediaPayment.TARJETA_CREDITO)
            ),
            Promotion(
                id = "2",
                discount = 10.0,
                startDate = LocalDate.of(2024, 1, 1),
                endDate = LocalDate.of(2024, 12, 31),
                active = true,
                mediaPayment = listOf(MediaPayment.TARJETA_DEBITO)
            ),
            Promotion(
                id = "3",
                discount = 10.0,
                startDate = LocalDate.of(2024, 1, 1),
                endDate = LocalDate.of(2024, 12, 31),
                active = true,
                mediaPayment = listOf(MediaPayment.EJECTIVO)
            ),
            Promotion(
                id = "3",
                discount = 10.0,
                startDate = LocalDate.of(2024, 1, 1),
                endDate = LocalDate.of(2024, 12, 31),
                active = true,
                mediaPayment = listOf(MediaPayment.GIFT_CARD)
            )
        )

        val expectedPromotions = listOf(
            Promotion(
                id = "1",
                discount = 10.0,
                startDate = LocalDate.of(2024, 1, 1),
                endDate = LocalDate.of(2024, 12, 31),
                active = true,
                mediaPayment = listOf(MediaPayment.TARJETA_CREDITO)
            )
        )

        // Configurar mock
        every { repository.findByActiveAndDateRange(true, date) } returns activePromotions

        // Ejecutar
        val result = queryService.findActiveByPurchase(
            date, listOf(MediaPayment.TARJETA_CREDITO), listOf(), listOf())

        // Verificar
        assertEquals(expectedPromotions, result)
        verify { repository.findByActiveAndDateRange(true, date) }
    }
}
