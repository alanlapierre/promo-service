package com.alapierre.api.rest

import com.alapierre.application.exception.PromotionNotFoundException
import com.fasterxml.jackson.core.JsonParseException
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Produces
import io.micronaut.http.server.exceptions.ExceptionHandler
import io.micronaut.http.server.exceptions.JsonExceptionHandler
import io.micronaut.serde.annotation.Serdeable.Deserializable
import io.micronaut.serde.annotation.Serdeable.Serializable
import jakarta.inject.Singleton

@Singleton
@Produces
class GlobalExceptionHandler : ExceptionHandler<Exception, HttpResponse<ErrorResponse>> {

    override fun handle(request: HttpRequest<*>, exception: Exception): HttpResponse<ErrorResponse> {
        return when (exception) {
            is PromotionNotFoundException -> {
                HttpResponse.notFound(ErrorResponse(exception.message ?: "Promotion Not Found"))
            }
            is IllegalArgumentException -> {
                // Bad requests generados en el servicio
                HttpResponse.badRequest(ErrorResponse(exception.message ?: "Bad Request"))
            }
            else -> {
                // Otros errores no esperados
                HttpResponse.serverError(ErrorResponse("Internal Server Error", listOf(exception.message)))
            }
        }
    }
}


@Serializable
@Deserializable
data class ErrorResponse(
    val message: String,
    val details: List<String?>? = null
)