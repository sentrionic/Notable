package io.notable.note_interactors

import io.notable.constants.ErrorHandling
import io.notable.core.domain.DataState
import io.notable.core.domain.ProgressBarState
import io.notable.core.domain.UIComponent
import io.notable.note_datasource.cache.NoteCache
import io.notable.note_domain.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Retrieve a note from the cache using the note's unique id
 */
class GetNoteFromCache(
    private val cache: NoteCache
) {

    fun execute(
        id: String,
    ): Flow<DataState<Note>> = flow {
        try {
            emit(DataState.Loading(progressBarState = ProgressBarState.Loading))

            // emit data from network
            val cachedNote =
                cache.getNote(id) ?: throw Exception(ErrorHandling.NOTE_DOES_NOT_EXIST)

            emit(DataState.Data(cachedNote))
        } catch (e: Exception) {
            e.printStackTrace()
            emit(
                DataState.Response<Note>(
                    uiComponent = UIComponent.Dialog(
                        title = "Error",
                        description = e.message ?: "Unknown error"
                    )
                )
            )
        } finally {
            emit(DataState.Loading(progressBarState = ProgressBarState.Idle))
        }
    }
}