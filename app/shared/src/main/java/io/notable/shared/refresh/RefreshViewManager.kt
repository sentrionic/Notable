package io.notable.shared.refresh

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RefreshViewManager
@Inject
constructor(
) {
    val state: MutableState<RefreshViewState> = mutableStateOf(RefreshViewState())

    fun onTriggerEvent(event: RefreshViewEvents) {
        when (event) {
            is RefreshViewEvents.DeleteNote -> {
                state.value = state.value.copy(
                    noteToRemove = event.id
                )
            }

            is RefreshViewEvents.AddNote -> {
                state.value = state.value.copy(
                    noteToAdd = event.note
                )
            }

            is RefreshViewEvents.RefreshDetail -> {
                state.value = state.value.copy(
                    refreshDetail = event.refresh
                )
            }
        }
    }
}