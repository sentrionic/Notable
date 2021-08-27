package io.notable.note_interactors

import io.notable.constants.ErrorHandling
import io.notable.core.domain.DataState
import io.notable.core.domain.ProgressBarState
import io.notable.core.domain.UIComponent
import io.notable.note_datasource.cache.NoteCache
import io.notable.note_datasource.network.NoteService
import io.notable.note_domain.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SyncDeletedNotes(
    private val service: NoteService,
    private val cache: NoteCache,
) {

    fun execute(
        token: String,
        isNetworkAvailable: Boolean,
    ): Flow<DataState<Unit>> = flow {
        try {
            emit(DataState.Loading(progressBarState = ProgressBarState.Loading))

            // 1. If network is available, fetch deleted notes
            if (isNetworkAvailable) {
                val deletedNotes: List<Note> = try { // catch network exceptions
                    service.getDeletedNotes(token = token)
                } catch (e: Exception) {
                    e.printStackTrace() // log to crashlytics?

                    val message = NoteService.getErrorMessage(e)

                    emit(
                        DataState.Response<Unit>(
                            uiComponent = UIComponent.Dialog(
                                title = ErrorHandling.NETWORK_DATA_ERROR,
                                description = message
                            )
                        )
                    )
                    listOf()
                }

                // 2. Delete those notes from the cache
                val ids = deletedNotes.map { it.id }
                cache.deleteNotes(ids)
            }

            emit(DataState.Data<Unit>())
        } catch (e: Exception) {
            e.printStackTrace()
            emit(
                DataState.Response<Unit>(
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