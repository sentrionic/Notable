package io.notable.auth_datasource_test.network

import io.notable.auth_datasource.network.model.AuthDto
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

private val json = Json {
    ignoreUnknownKeys = true
}

fun serializeAuthData(jsonData: String): AuthDto {
    return json.decodeFromString(jsonData)
}