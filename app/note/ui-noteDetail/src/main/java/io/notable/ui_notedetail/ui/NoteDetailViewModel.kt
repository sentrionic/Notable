package io.notable.ui_notedetail.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.notable.core.domain.*
import io.notable.note_interactors.DeleteNote
import io.notable.note_interactors.GetNoteFromCache
import io.notable.shared.refresh.RefreshViewEvents
import io.notable.shared.refresh.RefreshViewManager
import io.notable.shared.session.SessionManager
import io.notable.shared.util.ConnectivityManager
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class NoteDetailViewModel
@Inject
constructor(
    private val getNoteFromCache: GetNoteFromCache,
    private val deleteNote: DeleteNote,
    savedStateHandle: SavedStateHandle,
    private val refreshViewManager: RefreshViewManager,
    private val sessionManager: SessionManager,
    private val connectivityManager: ConnectivityManager,
    private val logger: Logger,
) : ViewModel() {

    val state: MutableState<NoteDetailState> = mutableStateOf(NoteDetailState())

    init {
        savedStateHandle.get<String>("noteId")?.let { noteId ->
            onTriggerEvent(NoteDetailEvents.GetNoteFromCache(noteId))
        }
    }

    fun onTriggerEvent(event: NoteDetailEvents) {
        when (event) {
            is NoteDetailEvents.GetNoteFromCache -> {
                getNoteFromCache(event.id)
            }
            is NoteDetailEvents.OnDeleteNote -> {
                confirmDelete(event.id)
            }
            NoteDetailEvents.OnRemoveHeadFromQueue -> {
                removeHeadMessage()
            }
        }
    }

    private fun getNoteFromCache(id: String) {
        getNoteFromCache.execute(id).onEach { dataState ->
            when (dataState) {
                is DataState.Loading -> {
                    state.value = state.value.copy(progressBarState = dataState.progressBarState)
                }
                is DataState.Data -> {
                    state.value = state.value.copy(note = dataState.data)
                }
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
            }
        }.launchIn(viewModelScope)
    }

    private fun confirmDelete(id: String) {
        val callback: AreYouSureCallback = object : AreYouSureCallback {
            override fun proceed() {
                handleDelete(id)
            }

            override fun cancel() {}
        }

        val message = UIComponent.AreYouSureDialog(
            message = "Delete Note",
            description = "Are you sure you want to delete this note? This action cannot be undone",
            callback = callback,
        )

        appendToMessageQueue(message)
    }

    private fun handleDelete(id: String) {
        sessionManager.state.value?.authToken?.let { token ->
            deleteNote.execute(
                token = token,
                isNetworkAvailable = connectivityManager.isNetworkAvailable.value,
                id = id
            ).onEach { dataState ->
                when (dataState) {
                    is DataState.Loading -> {
                        state.value =
                            state.value.copy(progressBarState = dataState.progressBarState)
                    }
                    is DataState.Data -> {
                        state.value = state.value.copy(
                            isDeleted = true
                        )
                        refreshViewManager.onTriggerEvent(RefreshViewEvents.DeleteNote(id))
                    }
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
}