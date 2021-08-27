package io.notable.ui_notedetail.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.notable.shared.R
import io.notable.shared.components.DefaultScreenUI
import io.notable.shared.components.MarkdownText
import io.notable.shared.refresh.RefreshViewEvents
import io.notable.shared.refresh.RefreshViewManager
import io.notable.ui_notedetail.ui.components.DetailAppBar

@Composable
fun NoteDetail(
    state: NoteDetailState,
    events: (NoteDetailEvents) -> Unit,
    navigateBack: () -> Unit,
    navigateToFormScreen: (String) -> Unit,
    refreshViewManager: RefreshViewManager
) {
    val alreadyExecuted = remember { mutableStateOf(false) }

    state.isDeleted.let {
        if (it && !alreadyExecuted.value) {
            alreadyExecuted.value = true
            navigateBack()
        }
    }

    refreshViewManager.state.value.let { value ->
        if (value.refreshDetail) {
            events(NoteDetailEvents.GetNoteFromCache(state.note?.id ?: ""))
            refreshViewManager.onTriggerEvent(RefreshViewEvents.RefreshDetail(false))
        }
    }

    DefaultScreenUI(
        queue = state.errorQueue,
        onRemoveHeadFromQueue = {
            events(NoteDetailEvents.OnRemoveHeadFromQueue)
        },
        progressBarState = state.progressBarState,
        topBar = {
            DetailAppBar(
                title = state.note?.title ?: "",
                navigateBack = navigateBack,
                handleEdit = { navigateToFormScreen(state.note?.id ?: "") },
                handleDelete = { events(NoteDetailEvents.OnDeleteNote(state.note?.id ?: "")) },
            )
        },
    ) {
        state.note?.let { note ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                item {

                    if (note.body.isNotEmpty()) {
                        MarkdownText(
                            modifier = Modifier
                                .padding(20.dp),
                            fontResource = R.font.mulish_regular,
                            markdown = note.body,
                        )
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Nothing here yet",
                                style = MaterialTheme.typography.h5
                            )
                        }
                    }
                }
            }
        }
    }
}