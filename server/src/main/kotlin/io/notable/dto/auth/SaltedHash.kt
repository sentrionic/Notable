package io.notable.dto.auth

data class SaltedHash(
    val hash: String,
    val salt: String
)
