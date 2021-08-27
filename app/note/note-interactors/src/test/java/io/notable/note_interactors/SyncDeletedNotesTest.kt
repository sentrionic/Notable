package io.notable.note_interactors

import io.notable.constants.ErrorHandling
import io.notable.core.domain.DataState
import io.notable.core.domain.ProgressBarState
import io.notable.core.domain.UIComponent
import io.notable.note_datasource_test.cache.NoteCacheFake
import io.notable.note_datasource_test.cache.NoteDatabaseFake
import io.notable.note_datasource_test.network.NoteServiceListFake
import io.notable.note_datasource_test.network.NoteServiceResponseTypeList
import io.notable.note_datasource_test.network.data.NoteDataValid
import io.notable.note_datasource_test.network.serializeNoteData
import io.notable.note_domain.Note
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Test

class SyncDeletedNotesTest {

    // system in test
    private lateinit var syncDeletedNotes: SyncDeletedNotes
    private val token = "123lkasoiasio"

    @Test
    fun deleteNetworkNotes_confirmCacheSync() = runBlocking {
        // setup
        val noteDatabase = NoteDatabaseFake()
        val noteCache = NoteCacheFake(noteDatabase)
        val noteService = NoteServiceListFake.build(
            type = NoteServiceResponseTypeList.GoodData // good data
        )

        syncDeletedNotes = SyncDeletedNotes(
            cache = noteCache,
            service = noteService
        )

        // Insert data into the cache
        val noteData = serializeNoteData(NoteDataValid.listData)
        noteCache.insert(noteData)

        // Confirm the cache is not empty anymore
        var cachedNotes = noteCache.fetchAll()
        assert(cachedNotes.size == NoteDataValid.NUM_NOTES)

        // Execute the use-case
        val emissions = syncDeletedNotes.execute(
            token = token,
            isNetworkAvailable = true
        ).toList()

        // First emission should be loading
        assert(emissions[0] == DataState.Loading<List<Note>>(ProgressBarState.Loading))

        // Confirm second emission is data
        assert(emissions[1] is DataState.Data)

        // Confirm the cache is empty now
        cachedNotes = noteCache.fetchAll()
        assert(cachedNotes.isEmpty())

        // Confirm loading state is IDLE
        assert(emissions[2] == DataState.Loading<List<Note>>(ProgressBarState.Idle))
    }

    /**
     * 1. Insert some data into the cache by executing a successful use-case.
     * 2. Configure the network operation to return malformed data.
     * 3. Execute use-case for a second time and confirm it still emits the cached data.
     */
    @Test
    fun syncDeletedNotes_malformedData() = runBlocking {
        // setup
        val noteDatabase = NoteDatabaseFake()
        val noteCache = NoteCacheFake(noteDatabase)
        val noteService = NoteServiceListFake.build(
            type = NoteServiceResponseTypeList.MalformedData // Malformed Data
        )

        syncDeletedNotes = SyncDeletedNotes(
            cache = noteCache,
            service = noteService
        )

        // Insert data into the cache
        val noteData = serializeNoteData(NoteDataValid.listData)
        noteCache.insert(noteData)

        // Confirm the cache is not empty anymore
        var cachedNotes = noteCache.fetchAll()
        assert(cachedNotes.size == NoteDataValid.NUM_NOTES)

        // Execute the use-case
        val emissions = syncDeletedNotes.execute(
            token = token,
            isNetworkAvailable = true
        ).toList()

        // First emission should be loading
        assert(emissions[0] == DataState.Loading<List<Note>>(ProgressBarState.Loading))

        // Confirm second emission is error response
        assert(emissions[1] is DataState.Response)
        assert(((emissions[1] as DataState.Response).uiComponent as UIComponent.Dialog).title == ErrorHandling.NETWORK_DATA_ERROR)
        assert(
            ((emissions[1] as DataState.Response).uiComponent as UIComponent.Dialog).description == ErrorHandling.UNKNOWN_ERROR
        )

        // Confirm third emission is data from the cache
        assert(emissions[2] is DataState.Data)

        // Confirm the cache is unchanged
        cachedNotes = noteCache.fetchAll()
        assert(cachedNotes.size == NoteDataValid.NUM_NOTES)

        // Confirm loading state is IDLE
        assert(emissions[3] == DataState.Loading<List<Note>>(ProgressBarState.Idle))
    }

    @Test
    fun syncDeletedNotes_emptyList() = runBlocking {
        // setup
        val noteDatabase = NoteDatabaseFake()
        val noteCache = NoteCacheFake(noteDatabase)
        val noteService = NoteServiceListFake.build(
            type = NoteServiceResponseTypeList.EmptyList // Empty List
        )

        syncDeletedNotes = SyncDeletedNotes(
            cache = noteCache,
            service = noteService
        )

        // Insert data into the cache
        val noteData = serializeNoteData(NoteDataValid.listData)
        noteCache.insert(noteData)

        // Confirm the cache is not empty anymore
        var cachedNotes = noteCache.fetchAll()
        assert(cachedNotes.size == NoteDataValid.NUM_NOTES)

        // Execute the use-case
        val emissions = syncDeletedNotes.execute(
            token = token,
            isNetworkAvailable = true
        ).toList()

        // First emission should be loading
        assert(emissions[0] == DataState.Loading<List<Note>>(ProgressBarState.Loading))

        // Confirm second emission is data (empty list)
        assert(emissions[1] is DataState.Data)

        // Confirm the cache is unchanged
        cachedNotes = noteCache.fetchAll()
        assert(cachedNotes.size == NoteDataValid.NUM_NOTES)

        // Confirm loading state is IDLE
        assert(emissions[2] == DataState.Loading<List<Note>>(ProgressBarState.Idle))
    }

    @Test
    fun syncDeletedNotes_notInternet() = runBlocking {
        // setup
        val noteDatabase = NoteDatabaseFake()
        val noteCache = NoteCacheFake(noteDatabase)
        val noteService = NoteServiceListFake.build(
            type = NoteServiceResponseTypeList.EmptyList // Empty List
        )

        syncDeletedNotes = SyncDeletedNotes(
            cache = noteCache,
            service = noteService
        )

        // Insert data into the cache
        val noteData = serializeNoteData(NoteDataValid.listData)
        noteCache.insert(noteData)

        // Confirm the cache is not empty anymore
        var cachedNotes = noteCache.fetchAll()
        assert(cachedNotes.size == NoteDataValid.NUM_NOTES)

        // Execute the use-case
        val emissions = syncDeletedNotes.execute(
            token = token,
            isNetworkAvailable = false
        ).toList()

        // First emission should be loading
        assert(emissions[0] == DataState.Loading<List<Note>>(ProgressBarState.Loading))

        // Confirm second emission is data (empty list)
        assert(emissions[1] is DataState.Data)

        // Confirm the cache is unchanged
        cachedNotes = noteCache.fetchAll()
        assert(cachedNotes.size == NoteDataValid.NUM_NOTES)

        // Confirm loading state is IDLE
        assert(emissions[2] == DataState.Loading<List<Note>>(ProgressBarState.Idle))
    }

    @Test
    fun syncDeletedNotes_serverError() = runBlocking {
        // setup
        val noteDatabase = NoteDatabaseFake()
        val noteCache = NoteCacheFake(noteDatabase)
        val noteService = NoteServiceListFake.build(
            type = NoteServiceResponseTypeList.ServerError // Empty List
        )

        syncDeletedNotes = SyncDeletedNotes(
            cache = noteCache,
            service = noteService
        )

        // Insert data into the cache
        val noteData = serializeNoteData(NoteDataValid.listData)
        noteCache.insert(noteData)

        // Confirm the cache is not empty anymore
        var cachedNotes = noteCache.fetchAll()
        assert(cachedNotes.size == NoteDataValid.NUM_NOTES)

        // Execute the use-case
        val emissions = syncDeletedNotes.execute(
            token = token,
            isNetworkAvailable = true
        ).toList()

        // First emission should be loading
        assert(emissions[0] == DataState.Loading<List<Note>>(ProgressBarState.Loading))

        assert(emissions[1] is DataState.Response)
        assert(((emissions[1] as DataState.Response).uiComponent as UIComponent.Dialog).title == ErrorHandling.NETWORK_DATA_ERROR)
        assert(
            ((emissions[1] as DataState.Response).uiComponent as UIComponent.Dialog).description == ErrorHandling.SERVER_ERROR
        )

        // Confirm second emission is data (empty list)
        assert(emissions[2] is DataState.Data)

        // Confirm the cache is unchanged
        cachedNotes = noteCache.fetchAll()
        assert(cachedNotes.size == NoteDataValid.NUM_NOTES)

        // Confirm loading state is IDLE
        assert(emissions[3] == DataState.Loading<List<Note>>(ProgressBarState.Idle))
    }

}