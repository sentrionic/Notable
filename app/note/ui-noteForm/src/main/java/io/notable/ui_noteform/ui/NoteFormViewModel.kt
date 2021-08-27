package io.notable.ui_noteform.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.notable.constants.INVALID_ID
import io.notable.core.domain.DataState
import io.notable.core.domain.KQueue
import io.notable.core.domain.Logger
import io.notable.core.domain.UIComponent
import io.notable.note_interactors.CreateNote
import io.notable.note_interactors.GetNoteFromCache
import io.notable.note_interactors.UpdateNote
import io.notable.shared.refresh.RefreshViewEvents
import io.notable.shared.refresh.RefreshViewManager
import io.notable.shared.session.SessionManager
import io.notable.shared.util.ConnectivityManager
import io.notable.ui_noteform.validation.TitleTextState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class NoteFormViewModel
@Inject
constructor(
    private val getNoteFromCache: GetNoteFromCache,
    private val createNote: CreateNote,
    private val updateNote: UpdateNote,
    private val refreshViewManager: RefreshViewManager,
    private val sessionManager: SessionManager,
    private val connectivityManager: ConnectivityManager,
    savedStateHandle: SavedStateHandle,
    private val logger: Logger,
) : ViewModel() {

    val state: MutableState<NoteFormState> = mutableStateOf(NoteFormState())

    init {
        savedStateHandle.get<String>("noteId")?.let { noteId ->
            onTriggerEvent(NoteFormEvents.GetNoteFromCache(noteId))
        }
    }

    fun onTriggerEvent(event: NoteFormEvents) {
        when (event) {
            is NoteFormEvents.GetNoteFromCache -> {
                getNoteFromCache(event.id)
            }
            is NoteFormEvents.OnUpdateBody -> {
                state.value = state.value.copy(body = event.body, isDirty = true)
            }
            is NoteFormEvents.OnUpdateTitle -> {
                state.value = state.value.copy(isDirty = true)
            }
            NoteFormEvents.OnSaveForm -> {
                if (state.value.note != null) {
                    onUpdateForm()
                } else {
                    onSaveForm()
                }
            }
            NoteFormEvents.OnRemoveHeadFromQueue -> {
                removeHeadMessage()
            }
            is NoteFormEvents.OnMessageReceived -> {
                appendToMessageQueue(event.uiComponent)
            }
        }
    }

    private fun getNoteFromCache(id: String) {
        if (id != INVALID_ID) {
            getNoteFromCache.execute(id).onEach { dataState ->
                when (dataState) {
                    is DataState.Loading -> {
                        state.value =
                            state.value.copy(progressBarState = dataState.progressBarState)
                    }
                    is DataState.Data -> {
                        val note = dataState.data
                        state.value = state.value.copy(
                            note = note,
                            title = TitleTextState().apply { text = note?.title ?: "" },
                            body = note?.body
                        )
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

    private fun onSaveForm() {
        sessionManager.state.value?.authToken?.let { token ->
            createNote.execute(
                token = token,
                isNetworkAvailable = connectivityManager.isNetworkAvailable.value,
                title = state.value.title.text,
                body = state.value.body ?: ""
            ).onEach { dataState ->
                when (dataState) {
                    is DataState.Loading -> {
                        state.value =
                            state.value.copy(progressBarState = dataState.progressBarState)
                    }
                    is DataState.Data -> {
                        state.value = state.value.copy(note = dataState.data, isDirty = false)
                        refreshViewManager.onTriggerEvent(RefreshViewEvents.AddNote(dataState.data))
                    }
                    is DataState.Response -> {
                        when (dataState.uiComponent) {
                            is UIComponent.Dialog,
                            is UIComponent.AreYouSureDialog,
                            is UIComponent.SnackBar -> {
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

    private fun onUpdateForm() {
        sessionManager.state.value?.authToken?.let { token ->
            updateNote.execute(
                token = token,
                isNetworkAvailable = connectivityManager.isNetworkAvailable.value,
                id = state.value.note?.id ?: "",
                title = state.value.title.text,
                body = state.value.body ?: ""
            ).onEach { dataState ->
                when (dataState) {
                    is DataState.Loading -> {
                        state.value =
                            state.value.copy(progressBarState = dataState.progressBarState)
                    }
                    is DataState.Data -> {
                        state.value = state.value.copy(note = dataState.data, isDirty = false)
                        refreshViewManager.onTriggerEvent(RefreshViewEvents.RefreshDetail(true))
                    }
                    is DataState.Response -> {
                        when (dataState.uiComponent) {
                            is UIComponent.Dialog,
                            is UIComponent.AreYouSureDialog,
                            is UIComponent.SnackBar -> {
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