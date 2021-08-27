package io.notable.auth_datasource.network

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.http.*
import io.notable.auth_datasource.network.model.AuthInput
import io.notable.constants.ErrorHandling

interface AuthService {

    suspend fun register(input: AuthInput): String

    suspend fun login(input: AuthInput): String

    companion object Factory {
        fun build(): AuthService {
            return AuthServiceImpl(
                httpClient = HttpClient(Android) {
                    install(JsonFeature) {
                        serializer = KotlinxSerializer(
                            kotlinx.serialization.json.Json {
                                ignoreUnknownKeys =
                                    true
                            }
                        )
                    }
                }
            )
        }

        fun getErrorMessage(e: Exception): String {
            return when (e) {
                is ClientRequestException -> {
                    when (e.response.status) {
                        HttpStatusCode.Forbidden -> ErrorHandling.EMAIL_ALREADY_IN_USE
                        HttpStatusCode.Unauthorized -> ErrorHandling.INVALID_CREDENTIALS
                        else -> ErrorHandling.UNKNOWN_ERROR
                    }
                }
                is ServerResponseException -> {
                    when (e.response.status) {
                        HttpStatusCode.InternalServerError -> ErrorHandling.SERVER_ERROR
                        else -> ErrorHandling.UNKNOWN_ERROR
                    }
                }
                else -> ErrorHandling.UNKNOWN_ERROR
            }
        }
    }
}