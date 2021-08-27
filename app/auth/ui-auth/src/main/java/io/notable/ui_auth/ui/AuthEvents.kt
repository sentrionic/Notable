package io.notable.ui_auth.ui

sealed class AuthEvents {

    object LoginPressed : AuthEvents()

    object RegisterPressed : AuthEvents()

    object OnRemoveHeadFromQueue : AuthEvents()
}