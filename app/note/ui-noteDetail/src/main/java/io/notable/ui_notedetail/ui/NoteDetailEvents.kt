package io.notable.ui_notedetail.ui

sealed class NoteDetailEvents {

    data class GetNoteFromCache(
        val id: String,
    ) : NoteDetailEvents()

    data class OnDeleteNote(
        val id: String,
    ) : NoteDetailEvents()

    object OnRemoveHeadFromQueue : NoteDetailEvents()
}