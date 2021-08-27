package io.notable.shared.refresh

import io.notable.note_domain.Note

data class RefreshViewState(
    val noteToAdd: Note? = null,
    val noteToRemove: String? = null,
    val refreshDetail: Boolean = false
)