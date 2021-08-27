package io.notable.note_datasource_test.network

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.http.*
import io.notable.note_datasource.network.NoteService
import io.notable.note_datasource.network.NoteServiceImpl
import io.notable.note_datasource_test.network.data.NoteDataEmpty
import io.notable.note_datasource_test.network.data.NoteDataMalformed
import io.notable.note_datasource_test.network.data.NoteDataValid

class NoteServiceListFake {

    companion object Factory {

        private val Url.hostWithPortIfRequired: String get() = if (port == protocol.defaultPort) host else hostWithPort
        private val Url.fullUrl: String get() = "${protocol.name}://$hostWithPortIfRequired$fullPath"

        private const val BASEURL = "http://192.168.2.123:8080"
        private val responseHeaders = headersOf(
            "Content-Type" to listOf("application/json", "charset=utf-8")
        )

        fun build(
            type: NoteServiceResponseTypeList
        ): NoteService {
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
                            "${BASEURL}/notes",
                            "${BASEURL}/notes/deleted" -> {
                                when (type) {
                                    is NoteServiceResponseTypeList.EmptyList -> {
                                        respond(
                                            NoteDataEmpty.data,
                                            status = HttpStatusCode.OK,
                                            headers = responseHeaders
                                        )
                                    }
                                    is NoteServiceResponseTypeList.MalformedData -> {
                                        respond(
                                            NoteDataMalformed.data,
                                            status = HttpStatusCode.OK,
                                            headers = responseHeaders
                                        )
                                    }
                                    is NoteServiceResponseTypeList.GoodData -> {
                                        respond(
                                            NoteDataValid.listData,
                                            status = HttpStatusCode.OK,
                                            headers = responseHeaders
                                        )
                                    }
                                    is NoteServiceResponseTypeList.GoodSingleData -> {
                                        respond(
                                            NoteDataValid.singleNote,
                                            status = HttpStatusCode.OK,
                                            headers = responseHeaders
                                        )
                                    }
                                    NoteServiceResponseTypeList.Unauthorized -> {
                                        respond(
                                            NoteDataEmpty.data,
                                            status = HttpStatusCode.Unauthorized,
                                            headers = responseHeaders
                                        )
                                    }
                                    NoteServiceResponseTypeList.ServerError -> {
                                        respond(
                                            "Server Error",
                                            status = HttpStatusCode.InternalServerError,
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
            return NoteServiceImpl(client)
        }
    }
}