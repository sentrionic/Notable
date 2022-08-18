package io.notable.note_datasource_test.network

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.http.*
import io.notable.constants.BASE_URL
import io.notable.note_datasource.network.NoteService
import io.notable.note_datasource.network.NoteServiceImpl
import io.notable.note_datasource_test.network.data.NoteDataEmpty
import io.notable.note_datasource_test.network.data.NoteDataValid

class NoteServiceSingleFake {

    companion object Factory {

        private val Url.hostWithPortIfRequired: String get() = if (port == protocol.defaultPort) host else hostWithPort
        private val Url.fullUrl: String get() = "${protocol.name}://$hostWithPortIfRequired$fullPath"

        private const val BASEURL = "$BASE_URL/notes"
        private val responseHeaders = headersOf(
            "Content-Type" to listOf("application/json", "charset=utf-8")
        )

        fun build(
            type: NoteServiceResponseTypeSingle
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
                            "${BASEURL}/067922ea-5ebc-491a-82ec-1660f21fbee7" -> {
                                when (type) {
                                    is NoteServiceResponseTypeSingle.Http404 -> {
                                        respond(
                                            NoteDataEmpty.data,
                                            status = HttpStatusCode.NotFound,
                                            headers = responseHeaders
                                        )
                                    }
                                    NoteServiceResponseTypeSingle.Unauthorized -> {
                                        respond(
                                            NoteDataEmpty.data,
                                            status = HttpStatusCode.Unauthorized,
                                            headers = responseHeaders
                                        )
                                    }
                                    NoteServiceResponseTypeSingle.ServerError -> {
                                        respond(
                                            "Server Error",
                                            status = HttpStatusCode.InternalServerError,
                                            headers = responseHeaders
                                        )
                                    }
                                    NoteServiceResponseTypeSingle.GoodData -> {
                                        respond(
                                            NoteDataValid.singleNote,
                                            status = HttpStatusCode.OK,
                                            headers = responseHeaders
                                        )
                                    }
                                    NoteServiceResponseTypeSingle.GoodUpdatedData -> {
                                        respond(
                                            NoteDataValid.singleNoteUpdated,
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
            return NoteServiceImpl(client)
        }
    }
}