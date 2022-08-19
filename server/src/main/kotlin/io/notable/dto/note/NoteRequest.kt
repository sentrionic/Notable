package io.notable.dto.note

import kotlinx.serialization.Serializable

@Serializable
data class NoteRequest(
    val title: String,
    val body: String?
)
