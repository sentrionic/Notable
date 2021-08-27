package io.notable.ui_noteform.ui

import io.notable.core.domain.KQueue
import io.notable.core.domain.ProgressBarState
import io.notable.core.domain.UIComponent
import io.notable.note_domain.Note
import io.notable.ui_noteform.validation.TitleTextState

data class NoteFormState(
    val progressBarState: ProgressBarState = ProgressBarState.Idle,
    val note: Note? = null,
    val errorQueue: KQueue<UIComponent> = KQueue(mutableListOf()),
    val title: TitleTextState = TitleTextState(),
    val body: String? = null,
    val isDirty: Boolean = false,
)