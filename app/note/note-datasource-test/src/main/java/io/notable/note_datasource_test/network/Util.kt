package io.notable.note_datasource_test.network

import io.notable.note_datasource.network.model.NoteDto
import io.notable.note_datasource.network.model.toNote
import io.notable.note_domain.Note
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

private val json = Json {
    ignoreUnknownKeys = true
}

fun serializeNoteData(jsonData: String): List<Note> {
    return json.decodeFromString<List<NoteDto>>(jsonData).map { it.toNote() }
}