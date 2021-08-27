package io.notable.ui_notedetail.ui

import io.notable.core.domain.KQueue
import io.notable.core.domain.ProgressBarState
import io.notable.core.domain.UIComponent
import io.notable.note_domain.Note

data class NoteDetailState(
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val note: Note? = null,
    val errorQueue: KQueue<UIComponent> = KQueue(mutableListOf()),
    val isDeleted: Boolean = false,
)