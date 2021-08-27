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

/**
 * 1. Delete success
 * 2. Delete success (post does not exist on server but does exist in cache)
 * 3. Delete failure (Server Error)
 */
class DeleteNoteTest {

    // system in test
    private lateinit var deleteNote: DeleteNote
    private val token = "aiasiodhahsidi"
    private val note = Data.note

    @Test
    fun deleteSuccess() = runBlocking {
        // setup
        val noteDatabase = NoteDatabaseFake()
        val noteCache = NoteCacheFake(noteDatabase)
        val noteService = NoteServiceSingleFake.build(
            type = NoteServiceResponseTypeSingle.GoodData
        )

        deleteNote = DeleteNote(
            service = noteService,
            cache = noteCache
        )

        // Ensure the note exists in the cache before deleting
        noteCache.insert(note)

        // confirm it exists in the cache
        var cachedNote = noteCache.getNote(note.id)
        assert(cachedNote == note)

        // delete the note
        val emissions = deleteNote.execute(
            token = token,
            isNetworkAvailable = true,
            id = note.id
        ).toList()

        // First emission should be loading
        assert(emissions[0] == DataState.Loading<Unit>(ProgressBarState.Loading))

        // confirm it was deleted from the cache
        cachedNote = noteCache.getNote(note.id)
        assert(cachedNote == null)

        // Confirm second emission is data
        assert(emissions[1] is DataState.Data)

        // Confirm third emission is the response
        assert((emissions[2] is DataState.Response))
        assert((emissions[2] as DataState.Response).uiComponent is UIComponent.SnackBar)
        assert(((emissions[2] as DataState.Response).uiComponent as UIComponent.SnackBar).message == SuccessHandling.DELETE_SUCCESS)

        // Confirm loading state is IDLE
        assert(emissions[3] == DataState.Loading<Unit>(ProgressBarState.Idle))
    }

    /**
     * Post exists in cache but does not exist on server.
     * We need to delete from cache.
     */
    @Test
    fun deleteSuccess_postDoesNotExist() = runBlocking {
        // setup
        val noteDatabase = NoteDatabaseFake()
        val noteCache = NoteCacheFake(noteDatabase)
        val noteService = NoteServiceSingleFake.build(
            type = NoteServiceResponseTypeSingle.Http404
        )

        deleteNote = DeleteNote(
            service = noteService,
            cache = noteCache
        )

        // Ensure the note exists in the cache before deleting
        noteCache.insert(note)

        // confirm it exists in the cache
        var cachedNote = noteCache.getNote(note.id)
        assert(cachedNote == note)

        // delete the note
        val emissions = deleteNote.execute(
            token = token,
            isNetworkAvailable = true,
            id = note.id
        ).toList()

        // First emission should be loading
        assert(emissions[0] == DataState.Loading<Unit>(ProgressBarState.Loading))

        // confirm it was deleted from the cache
        cachedNote = noteCache.getNote(note.id)
        assert(cachedNote == null)

        // Confirm second emission is data
        assert(emissions[1] is DataState.Data)

        // Confirm third emission is the response the user will see
        assert((emissions[2] is DataState.Response))
        assert((emissions[2] as DataState.Response).uiComponent is UIComponent.SnackBar)
        assert(((emissions[2] as DataState.Response).uiComponent as UIComponent.SnackBar).message == SuccessHandling.DELETE_SUCCESS)

        // Confirm loading state is IDLE
        assert(emissions[3] == DataState.Loading<Unit>(ProgressBarState.Idle))
    }

    @Test
    fun deleteFailure_ServerError() = runBlocking {
        // setup
        val noteDatabase = NoteDatabaseFake()
        val noteCache = NoteCacheFake(noteDatabase)
        val noteService = NoteServiceSingleFake.build(
            type = NoteServiceResponseTypeSingle.ServerError
        )

        deleteNote = DeleteNote(
            service = noteService,
            cache = noteCache
        )

        // Ensure the note exists in the cache before deleting
        noteCache.insert(note)

        // confirm it exists in the cache
        var cachedNote = noteCache.getNote(note.id)
        assert(cachedNote == note)

        // attempt to delete the note
        val emissions = deleteNote.execute(
            token = token,
            isNetworkAvailable = true,
            id = note.id
        ).toList()

        // First emission should be loading
        assert(emissions[0] == DataState.Loading<Note>(ProgressBarState.Loading))

        // confirm it's still in the cache
        cachedNote = noteCache.getNote(note.id)
        assert(cachedNote == note)

        // Confirm second emission is the response
        assert((emissions[1] is DataState.Response))
        assert((emissions[1] as DataState.Response).uiComponent is UIComponent.Dialog)
        assert(((emissions[1] as DataState.Response).uiComponent as UIComponent.Dialog).title == ErrorHandling.NETWORK_DATA_ERROR)
        assert(((emissions[1] as DataState.Response).uiComponent as UIComponent.Dialog).description == ErrorHandling.SERVER_ERROR)

        // Confirm loading state is IDLE
        assert(emissions[2] == DataState.Loading<Unit>(ProgressBarState.Idle))
    }

}