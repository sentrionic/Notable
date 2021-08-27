package io.notable.note_interactors

import io.notable.constants.ErrorHandling
import io.notable.constants.SuccessHandling
import io.notable.core.domain.DataState
import io.notable.core.domain.ProgressBarState
import io.notable.core.domain.UIComponent
import io.notable.note_datasource.cache.NoteCache
import io.notable.note_datasource.network.NoteService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class DeleteNote(
    private val service: NoteService,
    private val cache: NoteCache,
) {
    fun execute(
        token: String,
        isNetworkAvailable: Boolean,
        id: String
    ): Flow<DataState<Unit>> = flow {
        try {
            emit(DataState.Loading(progressBarState = ProgressBarState.Loading))

            if (isNetworkAvailable) {
                try {
                    // 1. Delete the note from the server
                    service.deleteNote(token = token, id = id)

                    // 2. Delete the note locally
                    cache.removeNote(id)

                    // 3. Emit success
                    emit(DataState.Data())
                    emit(
                        DataState.Response<Unit>(
                            uiComponent = UIComponent.SnackBar(
                                message = SuccessHandling.DELETE_SUCCESS
                            )
                        )
                    )

                } catch (e: Exception) {
                    e.printStackTrace()

                    val message = NoteService.getErrorMessage(e)

                    // Note does not exist on the server -> Delete it
                    if (message == ErrorHandling.NOTE_NOT_FOUND) {
                        cache.removeNote(id)

                        // 3. Emit success
                        emit(DataState.Data())
                        emit(
                            DataState.Response<Unit>(
                                uiComponent = UIComponent.SnackBar(
                                    message = SuccessHandling.DELETE_SUCCESS
                                )
                            )
                        )

                    } else {
                        emit(
                            DataState.Response<Unit>(
                                uiComponent = UIComponent.Dialog(
                                    title = ErrorHandling.NETWORK_DATA_ERROR,
                                    description = message
                                )
                            )
                        )
                    }
                }
            } else {
                // Short delay is needed or the dialog will not be shown
                delay(100)
                emit(
                    DataState.Response<Unit>(
                        uiComponent = UIComponent.Dialog(
                            title = ErrorHandling.NO_INTERNET,
                            description = ErrorHandling.NO_INTERNET_DESCRIPTION
                        )
                    )
                )
            }
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