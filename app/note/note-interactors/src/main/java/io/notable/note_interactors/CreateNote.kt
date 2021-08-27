package io.notable.note_interactors

import io.notable.constants.ErrorHandling
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
import java.util.*

class CreateNote(
    private val service: NoteService,
    private val cache: NoteCache,
) {
    fun execute(
        token: String,
        isNetworkAvailable: Boolean,
        title: String,
        body: String?,
    ): Flow<DataState<Note>> = flow {
        try {
            emit(DataState.Loading(progressBarState = ProgressBarState.Loading))

            // 1. Insert temp note into cache
            val date = Timestamp(System.currentTimeMillis()).toString().replace(" ", "T") + "0"

            val tempNote = Note(
                id = UUID.randomUUID().toString(),
                title = title,
                body = body ?: "",
                createdAt = date,
                updatedAt = date,
            )
            cache.insert(tempNote)

            // 2. Attempt to save the note on the server
            if (isNetworkAvailable) {
                val createdNote: Note? = try {
                    service.createNote(
                        token = token,
                        input = NoteInput(title, body)
                    )
                } catch (e: Exception) {
                    e.printStackTrace()

                    val message = NoteService.getErrorMessage(e)

                    emit(
                        DataState.Response(
                            uiComponent = UIComponent.Dialog(
                                title = ErrorHandling.NETWORK_DATA_ERROR,
                                description = message
                            )
                        )
                    )
                    null
                }

                // 3. Update the cached note
                if (createdNote != null) {
                    cache.removeNote(tempNote.id)
                    cache.insert(createdNote)

                    // 4. Return the network note
                    emit(DataState.Data(createdNote))
                    emit(
                        DataState.Response(
                            uiComponent = UIComponent.SnackBar(
                                message = SuccessHandling.SUCCESSFULLY_CREATED
                            )
                        )
                    )
                }
            } else {
                // 5. Return the offline note and inform the user
                emit(DataState.Data(tempNote))
                emit(
                    DataState.Response<Note>(
                        uiComponent = UIComponent.Dialog(
                            title = SuccessHandling.SUCCESSFULLY_CREATED_OFFLINE,
                            description = SuccessHandling.OFFLINE_DESCRIPTION
                        )
                    )
                )
            }
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