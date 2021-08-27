package io.notable.note_interactors

import io.notable.constants.ErrorHandling
import io.notable.core.domain.DataState
import io.notable.core.domain.ProgressBarState
import io.notable.core.domain.UIComponent
import io.notable.note_datasource.cache.NoteCache
import io.notable.note_datasource.network.NoteService
import io.notable.note_datasource.network.model.NoteInput
import io.notable.note_domain.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SyncNotes(
    private val service: NoteService,
    private val cache: NoteCache,
) {

    fun execute(
        token: String,
        isNetworkAvailable: Boolean
    ): Flow<DataState<List<Note>>> = flow {
        try {
            emit(DataState.Loading(progressBarState = ProgressBarState.Loading))

            // 1. Get cached notes
            val cachedNotes = cache.fetchAll().toMutableList()

            // 2. Get network notes
            val networkNotes = if (isNetworkAvailable) {
                try { // catch network exceptions
                    service.fetchNotes(token = token)
                } catch (e: Exception) {
                    e.printStackTrace() // log to crashlytics?

                    val message = NoteService.getErrorMessage(e)

                    emit(
                        DataState.Response(
                            uiComponent = UIComponent.Dialog(
                                title = ErrorHandling.NETWORK_DATA_ERROR,
                                description = message
                            )
                        )
                    )
                    listOf()
                }
            } else listOf()

            // 3. Sync network notes with the cached ones
            // - If network notes do not exist in the cache, insert them
            // - If they do exist in the cache, update them if they need to be updated
            // - Remove deleted cached notes from the server
            for (note in networkNotes) {
                try {
                    cache.getNote(note.id)?.let { cachedNote ->
                        cachedNotes.remove(cachedNote)
                        checkIfCachedNoteRequiresUpdate(
                            cachedNote = cachedNote,
                            networkNote = note,
                            token = token
                        )
                    } ?: cache.insert(note) // Test does not throw an exception
                } catch (e: Exception) {
                    // SQLDelight: Note cannot be found because it was not in the cache -> Insert it
                    cache.insert(note)
                }
            }

            // insert remaining into network
            if (isNetworkAvailable) {
                for (cachedNote in cachedNotes) {
                    val note: Note? = try {
                        service.createNote(
                            token = token,
                            input = NoteInput(title = cachedNote.title, body = cachedNote.body)
                        )
                    } catch (e: Exception) {
                        // log to crashlytics?
                        e.printStackTrace()
                        null
                    }
                    if (note != null) {
                        cache.removeNote(cachedNote.id)
                        cache.insert(note)
                    }
                }
            }

            // 3. Return the cached notes
            val notes = cache.fetchAll()

            emit(DataState.Data(notes))

        } catch (e: Exception) {
            e.printStackTrace()
            emit(
                DataState.Response<List<Note>>(
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

    private suspend fun checkIfCachedNoteRequiresUpdate(
        cachedNote: Note,
        networkNote: Note,
        token: String,
    ) {
        val cacheUpdatedAt = cachedNote.updatedAt
        val networkUpdatedAt = networkNote.updatedAt

        // update cache (network has newest data)
        if (networkUpdatedAt > cacheUpdatedAt) {
            cache.insert(networkNote)
        }
        // update network (cache has newest data)
        else if (networkUpdatedAt < cacheUpdatedAt) {
            service.updateNote(
                token = token,
                id = cachedNote.id,
                input = NoteInput(title = cachedNote.title, body = cachedNote.body)
            )
        }
    }
}