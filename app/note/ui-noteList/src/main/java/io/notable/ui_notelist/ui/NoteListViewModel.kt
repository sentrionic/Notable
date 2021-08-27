package io.notable.ui_notelist.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.notable.core.domain.DataState
import io.notable.core.domain.KQueue
import io.notable.core.domain.Logger
import io.notable.core.domain.UIComponent
import io.notable.note_domain.Note
import io.notable.note_domain.NoteFilter
import io.notable.note_interactors.ClearDatabase
import io.notable.note_interactors.FilterNotes
import io.notable.note_interactors.SyncDeletedNotes
import io.notable.note_interactors.SyncNotes
import io.notable.shared.session.SessionManager
import io.notable.shared.util.ConnectivityManager
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class NoteListViewModel
@Inject
constructor(
    private val syncNotes: SyncNotes,
    private val syncDeletedNotes: SyncDeletedNotes,
    private val filterNotes: FilterNotes,
    private val clearDatabase: ClearDatabase,
    private val sessionManager: SessionManager,
    private val connectivityManager: ConnectivityManager,
    private val logger: Logger,
) : ViewModel() {

    val state: MutableState<NoteListState> = mutableStateOf(NoteListState())

    init {
        onTriggerEvent(NoteListEvents.SyncNotes)
    }

    fun onTriggerEvent(event: NoteListEvents) {
        when (event) {
            is NoteListEvents.SyncNotes -> {
                syncDeletedNotes()
            }
            NoteListEvents.FilterNotes -> {
                filterNotes()
            }
            is NoteListEvents.UpdateQuery -> {
                updateQuery(event.query)
            }
            is NoteListEvents.UpdateNoteFilter -> {
                updateNoteFilter(event.noteFilter)
            }
            is NoteListEvents.UpdateFilterDialogState -> {
                state.value = state.value.copy(filterDialogState = event.uiComponentState)
            }
            is NoteListEvents.OnRemoveHeadFromQueue -> {
                removeHeadMessage()
            }
            is NoteListEvents.OnAddNote -> {
                addNoteToList(event.note)
            }
            is NoteListEvents.OnRemoveNote -> {
                removeNoteFromList(event.id)
            }
            NoteListEvents.OnClearDatabase -> {
                clearDB()
            }
        }
    }

    private fun updateQuery(query: String) {
        state.value = state.value.copy(query = query)
    }

    private fun updateNoteFilter(noteFilter: NoteFilter) {
        state.value = state.value.copy(noteFilter = noteFilter)
        filterNotes()
    }

    private fun filterNotes() {
        val filteredList = filterNotes.execute(
            current = state.value.notes,
            query = state.value.query,
            noteFilter = state.value.noteFilter
        )
        state.value = state.value.copy(filteredNotes = filteredList)
    }

    private fun getNotes() {
        sessionManager.state.value?.authToken?.let { token ->
            syncNotes.execute(
                token = token,
                isNetworkAvailable = connectivityManager.isNetworkAvailable.value
            ).onEach { dataState ->
                when (dataState) {
                    is DataState.Response -> {
                        when (dataState.uiComponent) {
                            is UIComponent.Dialog,
                            is UIComponent.SnackBar,
                            is UIComponent.AreYouSureDialog -> {
                                appendToMessageQueue(dataState.uiComponent)
                            }
                            is UIComponent.None -> {
                                logger.log((dataState.uiComponent as UIComponent.None).message)
                            }
                        }
                    }
                    is DataState.Data -> {
                        state.value = state.value.copy(notes = dataState.data ?: listOf())
                        filterNotes()
                    }
                    is DataState.Loading -> {
                        state.value =
                            state.value.copy(progressBarState = dataState.progressBarState)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun syncDeletedNotes() {
        sessionManager.state.value?.authToken?.let { token ->
            syncDeletedNotes.execute(
                token = token,
                isNetworkAvailable = connectivityManager.isNetworkAvailable.value
            ).onEach { dataState ->
                when (dataState) {
                    is DataState.Response -> {
                        when (dataState.uiComponent) {
                            is UIComponent.Dialog,
                            is UIComponent.SnackBar,
                            is UIComponent.AreYouSureDialog -> {
                                appendToMessageQueue(dataState.uiComponent)
                            }
                            is UIComponent.None -> {
                                logger.log((dataState.uiComponent as UIComponent.None).message)
                            }
                        }
                    }
                    is DataState.Data -> {
                        getNotes()
                    }
                    is DataState.Loading -> {
                        state.value =
                            state.value.copy(progressBarState = dataState.progressBarState)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun appendToMessageQueue(uiComponent: UIComponent) {
        val queue = state.value.errorQueue
        queue.add(uiComponent)
        state.value = state.value.copy(errorQueue = KQueue(mutableListOf())) // force recompose
        state.value = state.value.copy(errorQueue = queue)
    }

    private fun removeHeadMessage() {
        try {
            val queue = state.value.errorQueue
            queue.remove() // can throw exception if empty
            state.value = state.value.copy(errorQueue = KQueue(mutableListOf())) // force recompose
            state.value = state.value.copy(errorQueue = queue)
        } catch (e: Exception) {
            logger.log("Nothing to remove from DialogQueue")
        }
    }

    private fun addNoteToList(note: Note) {
        val current = ArrayList(state.value.notes)

        // Force recompose
        state.value = state.value.copy(
            notes = listOf(),
        )

        current.add(0, note)

        state.value = state.value.copy(
            notes = current,
        )

        filterNotes()
    }

    private fun removeNoteFromList(id: String) {
        val current = ArrayList(state.value.notes)

        // Force recompose
        state.value = state.value.copy(
            notes = listOf(),
        )

        current.find { it.id == id }?.let {
            current.remove(it)
        }

        state.value = state.value.copy(
            notes = current,
        )

        filterNotes()
    }

    private fun clearDB() {
        clearDatabase.execute().onEach { dataState ->
            when (dataState) {
                is DataState.Response -> {
                    when (dataState.uiComponent) {
                        is UIComponent.Dialog,
                        is UIComponent.SnackBar,
                        is UIComponent.AreYouSureDialog -> {
                            appendToMessageQueue(dataState.uiComponent)
                        }
                        is UIComponent.None -> {
                            logger.log((dataState.uiComponent as UIComponent.None).message)
                        }
                    }
                }
                is DataState.Data -> {
                }
                is DataState.Loading -> {
                    state.value =
                        state.value.copy(progressBarState = dataState.progressBarState)
                }
            }
        }.launchIn(viewModelScope)
    }
}
