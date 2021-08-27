package io.notable.shared.session

sealed class SessionEvents {

    object Logout : SessionEvents()

    data class Login(
        val token: String
    ) : SessionEvents()

}