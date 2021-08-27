package io.notable.auth_datasource.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AuthInput(
    @SerialName("email")
    val email: String,

    @SerialName("password")
    val password: String,
)