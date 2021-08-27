package io.notable.ui_notelist.ui

import io.notable.core.domain.UIComponentState
import io.notable.note_domain.Note
import io.notable.note_domain.NoteFilter

sealed class NoteListEvents {

    object SyncNotes : NoteListEvents()

    object FilterNotes : NoteListEvents()

    data class UpdateQuery(
        val query: String,
    ) : NoteListEvents()

    data class UpdateNoteFilter(
        val noteFilter: NoteFilter
    ) : NoteListEvents()

    data class UpdateFilterDialogState(
        val uiComponentState: UIComponentState
    ) : NoteListEvents()

    data class OnAddNote(
        val note: Note
    ) : NoteListEvents()

    data class OnRemoveNote(
        val id: String
    ) : NoteListEvents()

    object OnRemoveHeadFromQueue : NoteListEvents()

    object OnClearDatabase : NoteListEvents()
}