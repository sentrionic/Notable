package io.notable.auth_datasource.network

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.notable.auth_datasource.network.model.AuthDto
import io.notable.auth_datasource.network.model.AuthInput
import io.notable.constants.BASE_URL

class AuthServiceImpl(
    private val httpClient: HttpClient,
) : AuthService {

    override suspend fun register(input: AuthInput): String {
        return httpClient.post<AuthDto> {
            url("$BASE_URL/accounts")
            contentType(ContentType.Application.Json)
            body = input
        }.token
    }

    override suspend fun login(input: AuthInput): String {
        return httpClient.post<AuthDto> {
            url("$BASE_URL/accounts/login")
            contentType(ContentType.Application.Json)
            body = input
        }.token
    }

}