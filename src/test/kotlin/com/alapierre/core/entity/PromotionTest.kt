package com.alapierre.core.entity

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

class PromotionTest {

    @Test
    fun `should pass all rules for valid promotion`() {
        val promotion = Promotion(
            discount = 10.0,
            startDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 12, 31),
            active = true
        )

        assertDoesNotThrow { promotion.checkAllRules() }
    }

    @Test
    fun `should throw exception for discount less than minimum`() {
        val promotion = Promotion(
            discount = 3.0,
            startDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 12, 31)
        )

        val exception = assertThrows<IllegalArgumentException> {
            promotion.checkAllRules()
        }
        assert(exception.message == "El descuento no puede ser menor a 5% ni mayor a 80%")
    }

    @Test
    fun `should throw exception for discount greater than maximum`() {
        val promotion = Promotion(
            discount = 90.0,
            startDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 12, 31)
        )

        val exception = assertThrows<IllegalArgumentException> {
            promotion.checkAllRules()
        }
        assert(exception.message == "El descuento no puede ser menor a 5% ni mayor a 80%")
    }

    @Test
    fun `should throw exception for both discount and installments defined`() {
        val promotion = Promotion(
            discount = 10.0,
            installments = 6,
            startDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 12, 31)
        )

        val exception = assertThrows<IllegalArgumentException> {
            promotion.checkAllRules()
        }
        assert(exception.message == "Debe definirise cantidad de cuotas o porcentaje de descuento, pero no ambos")
    }

    @Test
    fun `should throw exception for interest without installments`() {
        val promotion = Promotion(
            interests = 10.0,
            discount = 10.0,
            startDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 12, 31)
        )

        val exception = assertThrows<IllegalArgumentException> {
            promotion.checkAllRules()
        }
        assert(exception.message == "El porcentaje de interés solo puede tener valor si cantidad de cuotas tiene valor")
    }

    @Test
    fun `should throw exception for missing start or end date`() {
        val promotionWithoutStartDate = Promotion(
            discount = 10.0,
            endDate = LocalDate.of(2024, 12, 31)
        )

        val exception1 = assertThrows<IllegalArgumentException> {
            promotionWithoutStartDate.checkAllRules()
        }
        assert(exception1.message == "La fecha de inicio y de fin deben estar presentes")

        val promotionWithoutEndDate = Promotion(
            discount = 10.0,
            startDate = LocalDate.of(2024, 1, 1)
        )

        val exception2 = assertThrows<IllegalArgumentException> {
            promotionWithoutEndDate.checkAllRules()
        }
        assert(exception2.message == "La fecha de inicio y de fin deben estar presentes")
    }

    @Test
    fun `should throw exception for end date before start date`() {
        val promotion = Promotion(
            discount = 10.0,
            startDate = LocalDate.of(2024, 12, 31),
            endDate = LocalDate.of(2024, 1, 1)
        )

        val exception = assertThrows<IllegalArgumentException> {
            promotion.checkAllRules()
        }
        assert(exception.message == "La fecha de fin no puede ser anterior a la fecha de inicio")
    }
}