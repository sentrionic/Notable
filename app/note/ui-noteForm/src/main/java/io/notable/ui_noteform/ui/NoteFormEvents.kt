package io.notable.ui_noteform.ui

import io.notable.core.domain.UIComponent

sealed class NoteFormEvents {

    data class GetNoteFromCache(
        val id: String,
    ) : NoteFormEvents()

    data class OnUpdateTitle(
        val title: String,
    ) : NoteFormEvents()

    data class OnUpdateBody(
        val body: String,
    ) : NoteFormEvents()

    data class OnMessageReceived(
        val uiComponent: UIComponent,
    ) : NoteFormEvents()

    object OnSaveForm : NoteFormEvents()

    object OnRemoveHeadFromQueue : NoteFormEvents()
}