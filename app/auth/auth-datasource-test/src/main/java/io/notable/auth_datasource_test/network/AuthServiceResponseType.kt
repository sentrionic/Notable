package io.notable.auth_datasource_test.network

sealed class AuthServiceResponseType {

    object GoodData : AuthServiceResponseType()

    object Unauthorized : AuthServiceResponseType()

    object Forbidden : AuthServiceResponseType()

    object ServerError : AuthServiceResponseType()
}