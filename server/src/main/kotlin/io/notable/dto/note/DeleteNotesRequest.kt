package io.notable.dto.note

import kotlinx.serialization.Serializable

@Serializable
data class DeleteNotesRequest(
    val ids: List<String>
)
