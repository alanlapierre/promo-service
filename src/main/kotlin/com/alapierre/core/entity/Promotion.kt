package com.alapierre.core.entity

import java.time.LocalDate

const val MIN_DISCOUNT = 5
const val MAX_DISCOUNT = 80


data class Promotion(
    val id: String? = null,

    val mediaPayment: List<MediaPayment> = listOf(),
    val bank: List<Bank> = listOf(),
    val productCategory:  List<ProductCategory> = listOf(),

    val installments: Int? = null,
    val interests: Double? = null,
    val discount: Double? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val active: Boolean = true,
    val creationDate: LocalDate = LocalDate.now(),
    val updateDate: LocalDate = LocalDate.now()) {

    fun checkAllRules() {
        require(checkDiscountValue(discount)) { "El descuento no puede ser menor a 5% ni mayor a 80%" }
        require(checkDiscountAndInstallments(discount, installments)) { "Debe definirise cantidad de cuotas o porcentaje de descuento, pero no ambos" }
        require(checkInterestAndInstallments(interests, installments)) { "El porcentaje de interés solo puede tener valor si cantidad de cuotas tiene valor" }
        require(checkStartAndEndDateArePresent(startDate, endDate)) { "La fecha de inicio y de fin deben estar presentes" }
        require(checkStartAndEndDate(startDate, endDate)) { "La fecha de fin no puede ser anterior a la fecha de inicio" }
    }

    private fun checkDiscountValue(discount: Double?): Boolean =
        !(discount != null && (discount < MIN_DISCOUNT || discount > MAX_DISCOUNT))

    private fun checkDiscountAndInstallments(discount: Double?, installments: Int?): Boolean =
        (discount != null) xor (installments != null)

    private fun checkInterestAndInstallments(interests: Double?, installments: Int?): Boolean =
        !(interests != null && installments == null)

    private fun checkStartAndEndDateArePresent(startDate: LocalDate?, endDate: LocalDate?): Boolean =
        !(startDate == null || endDate == null)

    private fun checkStartAndEndDate(startDate: LocalDate?, endDate: LocalDate?): Boolean =
        !(startDate != null && endDate!=null && endDate.isBefore(startDate))

}


enum class MediaPayment {
    TARJETA_CREDITO,
    TARJETA_DEBITO,
    EJECTIVO,
    GIFT_CARD
}

enum class Bank {
    GALICIA,
    SANTANDER_RIO,
    CIUDAD,
    NACION,
    ICBC,
    BBVA,
    MACRO
}

enum class ProductCategory {
    HOGAR,
    JARDIN,
    ELECTRO_COCINA,
    GRANDES_ELECTRO,
    COLCHONES,
    CELULARES,
    TECNOLOGIA,
    AUDIO
}