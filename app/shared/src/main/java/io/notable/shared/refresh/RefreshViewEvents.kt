package io.notable.shared.refresh

import io.notable.note_domain.Note

sealed class RefreshViewEvents {

    data class DeleteNote(val id: String?) : RefreshViewEvents()

    data class AddNote(val note: Note?) : RefreshViewEvents()

    data class RefreshDetail(val refresh: Boolean) : RefreshViewEvents()
}