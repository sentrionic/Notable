package io.notable.note_datasource.network.model

import io.notable.note_domain.Note
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NoteDto(

    @SerialName("id")
    val id: String,

    @SerialName("title")
    val title: String,

    @SerialName("body")
    val body: String,

    @SerialName("createdAt")
    val createdAt: String,

    @SerialName("updatedAt")
    val updatedAt: String,
)

fun NoteDto.toNote(): Note {
    return Note(
        id = id,
        title = title,
        body = body,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}