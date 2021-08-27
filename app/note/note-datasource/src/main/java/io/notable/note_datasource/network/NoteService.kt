package io.notable.note_datasource.network

import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.features.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.http.*
import io.notable.constants.ErrorHandling
import io.notable.note_datasource.network.model.NoteInput
import io.notable.note_domain.Note

interface NoteService {

    suspend fun fetchNotes(token: String): List<Note>

    suspend fun createNote(token: String, input: NoteInput): Note

    suspend fun updateNote(token: String, id: String, input: NoteInput): Note

    suspend fun deleteNote(token: String, id: String): Note

    suspend fun getDeletedNotes(token: String): List<Note>

    companion object Factory {
        fun build(): NoteService {
            return NoteServiceImpl(
                httpClient = HttpClient(Android) {
                    install(JsonFeature) {
                        serializer = KotlinxSerializer(
                            kotlinx.serialization.json.Json {
                                ignoreUnknownKeys =
                                    true // if the server sends extra fields, ignore them
                            }
                        )
                    }
                }
            )
        }

        fun getErrorMessage(e: Exception): String {
            return when (e) {
                is ClientRequestException -> {
                    when (e.response.status) {
                        HttpStatusCode.Unauthorized -> ErrorHandling.INVALID_TOKEN
                        HttpStatusCode.NotFound -> ErrorHandling.NOTE_NOT_FOUND
                        else -> ErrorHandling.UNKNOWN_ERROR
                    }
                }
                is ServerResponseException -> {
                    when (e.response.status) {
                        HttpStatusCode.InternalServerError -> ErrorHandling.SERVER_ERROR
                        else -> ErrorHandling.UNKNOWN_ERROR
                    }
                }
                else -> ErrorHandling.UNKNOWN_ERROR
            }
        }
    }
}