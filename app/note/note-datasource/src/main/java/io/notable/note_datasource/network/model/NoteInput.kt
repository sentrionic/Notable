package io.notable.note_datasource.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NoteInput(
    @SerialName("title")
    val title: String,

    @SerialName("body")
    val body: String?,
)