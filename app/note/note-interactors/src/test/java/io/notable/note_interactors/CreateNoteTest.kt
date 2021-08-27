package io.notable.note_interactors

import io.notable.constants.ErrorHandling
import io.notable.constants.SuccessHandling
import io.notable.core.domain.DataState
import io.notable.core.domain.ProgressBarState
import io.notable.core.domain.UIComponent
import io.notable.note_datasource_test.cache.NoteCacheFake
import io.notable.note_datasource_test.cache.NoteDatabaseFake
import io.notable.note_datasource_test.network.NoteServiceListFake
import io.notable.note_datasource_test.network.NoteServiceResponseTypeList
import io.notable.note_domain.Note
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 * 1. Create Note Success online and offline
 * 2. Create Note Success offline, but not inserted on the server
 * 3. Create Note Failure (Server Error)
 * 4. Create Note Failure (Invalid Credentials)
 */
class CreateNoteTest {
    // system in test
    private lateinit var createNote: CreateNote
    private val token = "12jsni8929und8i"
    private val title = "Untitled #20"
    private val body = "# Test Post"

    @Test
    fun createNote_success_confirmNetworkAndCacheUpdated() = runBlocking {
        // setup
        val noteDatabase = NoteDatabaseFake()
        val noteCache = NoteCacheFake(noteDatabase)
        val noteService = NoteServiceListFake.build(
            type = NoteServiceResponseTypeList.GoodSingleData
        )

        createNote = CreateNote(
            cache = noteCache,
            service = noteService
        )

        // Confirm the cache is empty before any use-cases have been executed
        var cachedNotes = noteCache.fetchAll()
        assert(cachedNotes.isEmpty())

        // Execute the use-case
        val emissions = createNote.execute(
            token = token,
            isNetworkAvailable = true,
            title = title,
            body = body
        ).toList()

        // First emission should be loading
        assert(emissions[0] == DataState.Loading<Note>(ProgressBarState.Loading))

        // Confirm second emission is data
        assert(emissions[1] is DataState.Data)
        assert((emissions[1] as DataState.Data).data?.title == title)
        assert((emissions[1] as DataState.Data).data?.body == body)

        // Confirm second emission is the response
        assert(emissions[2] is DataState.Response)
        assert(((emissions[2] as DataState.Response).uiComponent is UIComponent.SnackBar))
        assert(((emissions[2] as DataState.Response).uiComponent as UIComponent.SnackBar).message == SuccessHandling.SUCCESSFULLY_CREATED)

        // Confirm the cache is no longer empty
        cachedNotes = noteCache.fetchAll()
        assert(cachedNotes.size == 1)

        // Confirm loading state is IDLE
        assert(emissions[3] == DataState.Loading<Note>(ProgressBarState.Idle))
    }

    @Test
    fun createNote_offlineSuccess_notInsertedIntoService() = runBlocking {
        // setup
        val noteDatabase = NoteDatabaseFake()
        val noteCache = NoteCacheFake(noteDatabase)
        val noteService = NoteServiceListFake.build(
            type = NoteServiceResponseTypeList.GoodSingleData
        )

        createNote = CreateNote(
            cache = noteCache,
            service = noteService
        )

        // Confirm the cache is empty before any use-cases have been executed
        var cachedNotes = noteCache.fetchAll()
        assert(cachedNotes.isEmpty())

        // Execute the use-case
        val emissions = createNote.execute(
            token = token,
            isNetworkAvailable = false,
            title = title,
            body = body
        ).toList()

        // First emission should be loading
        assert(emissions[0] == DataState.Loading<Note>(ProgressBarState.Loading))

        // Confirm second emission is data
        assert(emissions[1] is DataState.Data)
        assert((emissions[1] as DataState.Data).data?.title == title)
        assert((emissions[1] as DataState.Data).data?.body == body)

        // Confirm second emission is the response
        assert(emissions[2] is DataState.Response)
        assert(((emissions[2] as DataState.Response).uiComponent is UIComponent.Dialog))
        assert(((emissions[2] as DataState.Response).uiComponent as UIComponent.Dialog).title == SuccessHandling.SUCCESSFULLY_CREATED_OFFLINE)
        assert(((emissions[2] as DataState.Response).uiComponent as UIComponent.Dialog).description == SuccessHandling.OFFLINE_DESCRIPTION)

        // Confirm the cache is no longer empty
        cachedNotes = noteCache.fetchAll()
        assert(cachedNotes.size == 1)

        // Confirm loading state is IDLE
        assert(emissions[3] == DataState.Loading<Note>(ProgressBarState.Idle))
    }

    @Test
    fun createNote_serverFailure() = runBlocking {
        // setup
        val noteDatabase = NoteDatabaseFake()
        val noteCache = NoteCacheFake(noteDatabase)
        val noteService = NoteServiceListFake.build(
            type = NoteServiceResponseTypeList.ServerError
        )

        createNote = CreateNote(
            cache = noteCache,
            service = noteService
        )

        // Confirm the cache is empty before any use-cases have been executed
        var cachedNotes = noteCache.fetchAll()
        assert(cachedNotes.isEmpty())

        // Execute the use-case
        val emissions = createNote.execute(
            token = token,
            isNetworkAvailable = true,
            title = title,
            body = body
        ).toList()

        // First emission should be loading
        assert(emissions[0] == DataState.Loading<Note>(ProgressBarState.Loading))

        // Confirm second emission is response
        assert(emissions[1] is DataState.Response)
        assert((emissions[1] as DataState.Response).uiComponent is UIComponent.Dialog)
        assert(((emissions[1] as DataState.Response).uiComponent as UIComponent.Dialog).title == ErrorHandling.NETWORK_DATA_ERROR)
        assert(((emissions[1] as DataState.Response).uiComponent as UIComponent.Dialog).description == ErrorHandling.SERVER_ERROR)

        // Confirm the cache contains the temp note
        cachedNotes = noteCache.fetchAll()
        assert(cachedNotes.size == 1)

        // Confirm loading state is IDLE
        assert(emissions[2] == DataState.Loading<Note>(ProgressBarState.Idle))
    }

    @Test
    fun createNote_unauthorized() = runBlocking {
        // setup
        val noteDatabase = NoteDatabaseFake()
        val noteCache = NoteCacheFake(noteDatabase)
        val noteService = NoteServiceListFake.build(
            type = NoteServiceResponseTypeList.Unauthorized
        )

        createNote = CreateNote(
            cache = noteCache,
            service = noteService
        )

        // Confirm the cache is empty before any use-cases have been executed
        var cachedNotes = noteCache.fetchAll()
        assert(cachedNotes.isEmpty())

        // Execute the use-case
        val emissions = createNote.execute(
            token = token,
            isNetworkAvailable = true,
            title = title,
            body = body
        ).toList()

        // First emission should be loading
        assert(emissions[0] == DataState.Loading<Note>(ProgressBarState.Loading))

        // Confirm second emission is data
        assert(emissions[1] is DataState.Response)
        assert((emissions[1] as DataState.Response).uiComponent is UIComponent.Dialog)
        assert(((emissions[1] as DataState.Response).uiComponent as UIComponent.Dialog).title == ErrorHandling.NETWORK_DATA_ERROR)
        assert(((emissions[1] as DataState.Response).uiComponent as UIComponent.Dialog).description == ErrorHandling.INVALID_TOKEN)

        // Confirm the cache contains the temp note
        cachedNotes = noteCache.fetchAll()
        assert(cachedNotes.size == 1)

        // Confirm loading state is IDLE
        assert(emissions[2] == DataState.Loading<Note>(ProgressBarState.Idle))
    }
}