package io.notable.note_interactors

import io.notable.constants.ErrorHandling
import io.notable.constants.SuccessHandling
import io.notable.core.domain.DataState
import io.notable.core.domain.ProgressBarState
import io.notable.core.domain.UIComponent
import io.notable.note_datasource_test.cache.NoteCacheFake
import io.notable.note_datasource_test.cache.NoteDatabaseFake
import io.notable.note_datasource_test.network.NoteServiceResponseTypeSingle
import io.notable.note_datasource_test.network.NoteServiceSingleFake
import io.notable.note_datasource_test.util.Data
import io.notable.note_domain.Note
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Test

class UpdateNoteTest {
    // system in test
    private lateinit var updateNote: UpdateNote

    private val note = Data.note
    private val token = "kskjskjsjahdiuo"

    private val title = "Untitled #20"
    private val body = "# Test Post Updated"

    @Test
    fun createNote_success() = runBlocking {
        // setup
        val noteDatabase = NoteDatabaseFake()
        val noteCache = NoteCacheFake(noteDatabase)
        val noteService = NoteServiceSingleFake.build(
            type = NoteServiceResponseTypeSingle.GoodUpdatedData
        )

        updateNote = UpdateNote(
            cache = noteCache,
            service = noteService
        )

        // Ensure the note exists in the cache before updating
        noteCache.insert(note)

        // confirm it exists in the cache
        var cachedNote = noteCache.getNote(note.id)
        assert(cachedNote == note)

        // Execute the use-case
        val emissions = updateNote.execute(
            token = token,
            isNetworkAvailable = true,
            id = note.id,
            title = title,
            body = body
        ).toList()

        // First emission should be loading
        assert(emissions[0] == DataState.Loading<Note>(ProgressBarState.Loading))

        // Confirm second emission is data
        assert(emissions[1] is DataState.Data)
        assert((emissions[1] as DataState.Data).data?.title == title)
        assert((emissions[1] as DataState.Data).data?.body == body)
        assert((emissions[1] as DataState.Data).data?.updatedAt ?: "" >= note.updatedAt)

        assert(emissions[2] is DataState.Response)
        assert((emissions[2] as DataState.Response).uiComponent is UIComponent.SnackBar)
        assert(((emissions[2] as DataState.Response).uiComponent as UIComponent.SnackBar).message == SuccessHandling.UPDATE_SUCCESS)

        // confirm it was updated
        cachedNote = noteCache.getNote(note.id)
        assert(cachedNote != note)

        // Confirm loading state is IDLE
        assert(emissions[3] == DataState.Loading<Note>(ProgressBarState.Idle))
    }

    @Test
    fun updateNote_updatedLocally_serverFailure() = runBlocking {
        // setup
        val noteDatabase = NoteDatabaseFake()
        val noteCache = NoteCacheFake(noteDatabase)
        val noteService = NoteServiceSingleFake.build(
            type = NoteServiceResponseTypeSingle.ServerError
        )

        updateNote = UpdateNote(
            cache = noteCache,
            service = noteService
        )

        // Ensure the note exists in the cache before updating
        noteCache.insert(note)

        // confirm it exists in the cache
        var cachedNote = noteCache.getNote(note.id)
        assert(cachedNote == note)

        // Execute the use-case
        val emissions = updateNote.execute(
            token = token,
            isNetworkAvailable = true,
            id = note.id,
            title = title,
            body = body
        ).toList()

        // First emission should be loading
        assert(emissions[0] == DataState.Loading<Note>(ProgressBarState.Loading))

        // Confirm second emission is the response
        assert((emissions[1] is DataState.Response))
        assert((emissions[1] as DataState.Response).uiComponent is UIComponent.None)
        assert(((emissions[1] as DataState.Response).uiComponent as UIComponent.None).message == ErrorHandling.SERVER_ERROR)

        // Confirm third emission is data
        assert(emissions[2] is DataState.Data)
        assert((emissions[2] as DataState.Data).data?.title == title)
        assert((emissions[2] as DataState.Data).data?.body == body)
        assert((emissions[2] as DataState.Data).data?.updatedAt ?: "" >= note.updatedAt)

        assert(emissions[3] is DataState.Response)
        assert((emissions[3] as DataState.Response).uiComponent is UIComponent.SnackBar)
        assert(((emissions[3] as DataState.Response).uiComponent as UIComponent.SnackBar).message == SuccessHandling.UPDATE_SUCCESS)

        // confirm it was updated
        cachedNote = noteCache.getNote(note.id)
        assert(cachedNote != note)

        // Confirm loading state is IDLE
        assert(emissions[4] == DataState.Loading<Note>(ProgressBarState.Idle))
    }
}