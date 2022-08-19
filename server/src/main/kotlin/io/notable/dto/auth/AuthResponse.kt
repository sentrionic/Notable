package io.notable.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String
)
