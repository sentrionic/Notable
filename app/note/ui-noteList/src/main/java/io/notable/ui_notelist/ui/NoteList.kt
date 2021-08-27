package io.notable.ui_notelist.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import io.notable.core.domain.ProgressBarState
import io.notable.core.domain.UIComponentState
import io.notable.shared.components.DefaultScreenUI
import io.notable.shared.refresh.RefreshViewEvents
import io.notable.shared.refresh.RefreshViewManager
import io.notable.ui_notelist.components.*
import io.notable.ui_notelist.ui.NoteListEvents.*

@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@Composable
fun NoteList(
    state: NoteListState,
    events: (NoteListEvents) -> Unit,
    navigateToDetailScreen: (String) -> Unit,
    navigateToFormScreen: () -> Unit,
    handleLogout: () -> Unit,
    refreshViewManager: RefreshViewManager,
) {
    refreshViewManager.state.value.let { value ->
        value.noteToAdd?.let { post ->
            events(OnAddNote(post))
            refreshViewManager.onTriggerEvent(RefreshViewEvents.AddNote(null))
        }

        value.noteToRemove?.let { id ->
            events(OnRemoveNote(id))
            refreshViewManager.onTriggerEvent(RefreshViewEvents.DeleteNote(null))
        }
    }

    val scaffoldState = rememberScaffoldState()

    DefaultScreenUI(
        queue = state.errorQueue,
        onRemoveHeadFromQueue = {
            events(OnRemoveHeadFromQueue)
        },
        progressBarState = state.progressBarState,
        floatingActionButton = {
            CreateNoteButton(
                navigateToFormScreen = navigateToFormScreen
            )
        },
        scaffoldState = scaffoldState,
        topBar = {
            NoteListTopBar(
                scaffoldState = scaffoldState,
            )
        },
        drawer = {
            NoteListDrawer(
                handleLogout = handleLogout
            )
        },
    ) {
        Column {
            NoteListToolbar(
                query = state.query,
                onQueryChanged = { query ->
                    events(UpdateQuery(query = query))
                },
                onExecuteSearch = {
                    events(FilterNotes)
                },
                onShowFilterDialog = {
                    events(UpdateFilterDialogState(UIComponentState.Show))
                },
            )

            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = state.isRefreshing),
                onRefresh = { events(SyncNotes) }
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (state.filteredNotes.isEmpty() && state.progressBarState != ProgressBarState.Loading) {
                        item {
                            NoteListPlaceholder()
                        }
                    } else {
                        items(state.filteredNotes) { note ->
                            NoteListItem(
                                note = note,
                                onSelectNote = { navigateToDetailScreen(it) }
                            )
                        }
                    }
                }
            }
        }

        if (state.filterDialogState is UIComponentState.Show) {
            NoteListFilter(
                noteFilter = state.noteFilter,
                onUpdateNoteFilter = { noteFilter ->
                    events(UpdateNoteFilter(noteFilter))
                },
                onCloseDialog = {
                    events(UpdateFilterDialogState(UIComponentState.Hide))
                }
            )
        }
    }
}