package io.notable.ui_notelist.ui

import io.notable.core.domain.*
import io.notable.note_domain.Note
import io.notable.note_domain.NoteFilter

data class NoteListState(
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val notes: List<Note> = listOf(),
    val filteredNotes: List<Note> = listOf(),
    val query: String = "",
    val isRefreshing: Boolean = false,
    val noteFilter: NoteFilter = NoteFilter.UpdatedAt(FilterOrder.Descending),
    val filterDialogState: UIComponentState = UIComponentState.Hide,
    val errorQueue: KQueue<UIComponent> = KQueue(mutableListOf())
)