package io.notable.auth_datasource_test.network

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.http.*
import io.notable.auth_datasource.network.AuthService
import io.notable.auth_datasource.network.AuthServiceImpl
import io.notable.auth_datasource_test.network.data.AuthDataEmpty
import io.notable.auth_datasource_test.network.data.AuthDataValid

class AuthServiceFake {

    companion object Factory {

        private val Url.hostWithPortIfRequired: String get() = if (port == protocol.defaultPort) host else hostWithPort
        private val Url.fullUrl: String get() = "${protocol.name}://$hostWithPortIfRequired$fullPath"

        private const val BASEURL = "http://192.168.2.123:8080/accounts"
        private val responseHeaders = headersOf(
            "Content-Type" to listOf("application/json", "charset=utf-8")
        )

        fun build(
            type: AuthServiceResponseType
        ): AuthService {
            val client = HttpClient(MockEngine) {
                install(JsonFeature) {
                    serializer = KotlinxSerializer(
                        kotlinx.serialization.json.Json {
                            ignoreUnknownKeys =
                                true // if the server sends extra fields, ignore them
                        }
                    )
                }
                engine {
                    addHandler { request ->
                        when (request.url.fullUrl) {
                            BASEURL,
                            "${BASEURL}/login" -> {
                                when (type) {
                                    AuthServiceResponseType.Forbidden -> {
                                        respond(
                                            AuthDataEmpty.data,
                                            status = HttpStatusCode.Forbidden,
                                            headers = responseHeaders
                                        )
                                    }
                                    AuthServiceResponseType.Unauthorized -> {
                                        respond(
                                            AuthDataEmpty.data,
                                            status = HttpStatusCode.Unauthorized,
                                            headers = responseHeaders
                                        )
                                    }
                                    AuthServiceResponseType.ServerError -> {
                                        respond(
                                            "Server Error",
                                            status = HttpStatusCode.InternalServerError,
                                            headers = responseHeaders
                                        )
                                    }
                                    AuthServiceResponseType.GoodData -> {
                                        respond(
                                            AuthDataValid.data,
                                            status = HttpStatusCode.OK,
                                            headers = responseHeaders
                                        )
                                    }
                                }
                            }
                            else -> error("Unhandled ${request.url.fullUrl}")
                        }
                    }
                }
            }
            return AuthServiceImpl(client)
        }
    }
}