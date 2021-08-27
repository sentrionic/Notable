package io.notable.auth_interactors

import io.notable.auth_datasource.network.AuthService

data class AuthInteractors(
    val login: Login,
    val register: Register,
) {
    companion object Factory {
        fun build(): AuthInteractors {
            val service = AuthService.build()
            return AuthInteractors(
                login = Login(
                    service = service,
                ),
                register = Register(
                    service = service,
                ),
            )
        }
    }
}