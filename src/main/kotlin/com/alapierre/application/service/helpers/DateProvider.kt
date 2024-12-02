package com.alapierre.application.service.helpers

import jakarta.inject.Singleton
import java.time.LocalDate

@Singleton
class DateProvider {
    open fun now(): LocalDate = LocalDate.now()
}