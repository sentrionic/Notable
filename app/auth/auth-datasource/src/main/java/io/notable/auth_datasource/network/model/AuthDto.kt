package io.notable.auth_datasource.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthDto(
    @SerialName("token")
    val token: String,
)
