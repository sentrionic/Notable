package io.notable.note_interactors

import io.notable.constants.SuccessHandling
import io.notable.core.domain.DataState
import io.notable.core.domain.ProgressBarState
import io.notable.core.domain.UIComponent
import io.notable.note_datasource.cache.NoteCache
import io.notable.note_datasource.network.NoteService
import io.notable.note_datasource.network.model.NoteInput
import io.notable.note_domain.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.sql.Timestamp

class UpdateNote(
    private val service: NoteService,
    private val cache: NoteCache,
) {
    fun execute(
        token: String,
        isNetworkAvailable: Boolean,
        id: String,
        title: String,
        body: String,
    ): Flow<DataState<Note>> = flow {
        try {
            emit(DataState.Loading(progressBarState = ProgressBarState.Loading))

            // 1. Update the note locally
            val date = Timestamp(System.currentTimeMillis()).toString().replace(" ", "T") + "000+02:00"
            cache.updateNote(id = id, title = title, body = body, updatedAt = date)

            // 2. Attempt to update the note on the server
            if (isNetworkAvailable) {
                val note: Note? = try {
                    service.updateNote(
                        token = token,
                        id = id,
                        input = NoteInput(title, body)
                    )
                } catch (e: Exception) {
                    e.printStackTrace()

                    val message = NoteService.getErrorMessage(e)

                    // Server Errors could be logged, but the note will be deleted on next sync,
                    // so no need to inform the user
                    emit(
                        DataState.Response<Note>(
                            uiComponent = UIComponent.None(
                                message = message,
                            )
                        )
                    )
                    null
                }

                if (note != null) {
                    cache.insert(note)
                }
            }

            // Return the updated note
            val note = cache.getNote(id)

            emit(DataState.Data(note))
            emit(
                DataState.Response<Note>(
                    uiComponent = UIComponent.SnackBar(
                        message = SuccessHandling.UPDATE_SUCCESS
                    )
                )
            )

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