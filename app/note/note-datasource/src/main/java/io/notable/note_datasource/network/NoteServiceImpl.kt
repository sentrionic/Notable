package io.notable.note_datasource.network

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.notable.note_datasource.network.model.NoteDto
import io.notable.note_datasource.network.model.NoteInput
import io.notable.note_datasource.network.model.toNote
import io.notable.note_domain.Note

class NoteServiceImpl(
    private val httpClient: HttpClient,
) : NoteService {

    override suspend fun fetchNotes(token: String): List<Note> {
        return httpClient.get<List<NoteDto>> {
            url(EndPoints.NOTES)
            header("Authorization", "Bearer $token")
        }.map { it.toNote() }
    }

    override suspend fun createNote(token: String, input: NoteInput): Note {
        return httpClient.post<NoteDto> {
            url(EndPoints.NOTES)
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            body = input
        }.toNote()
    }

    override suspend fun updateNote(token: String, id: String, input: NoteInput): Note {
        return httpClient.put<NoteDto> {
            url("${EndPoints.NOTES}/$id")
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            body = input
        }.toNote()
    }

    override suspend fun deleteNote(token: String, id: String): Note {
        return httpClient.delete<NoteDto> {
            url("${EndPoints.NOTES}/$id")
            header("Authorization", "Bearer $token")
        }.toNote()
    }

    override suspend fun getDeletedNotes(token: String): List<Note> {
        return httpClient.get<List<NoteDto>> {
            url("${EndPoints.NOTES}/deleted")
            header("Authorization", "Bearer $token")
        }.map { it.toNote() }
    }
}