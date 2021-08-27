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
import io.notable.note_datasource_test.network.data.NoteDataValid.NUM_NOTES
import io.notable.note_datasource_test.network.serializeNoteData
import io.notable.note_domain.Note
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Test

/**
 * 1. Sync Success (Inserted Network Notes)
 * 2. Sync Failure (Empty Data)
 * 3. Sync Failure (Malformed Data)
 */
class SyncNotesTest {

    // system in test
    private lateinit var syncNotes: SyncNotes
    private val token = "123lkasoiasio"

    @Test
    fun syncNotes_success() = runBlocking {
        // setup
        val noteDatabase = NoteDatabaseFake()
        val noteCache = NoteCacheFake(noteDatabase)
        val noteService = NoteServiceListFake.build(
            type = NoteServiceResponseTypeList.GoodData // good data
        )

        syncNotes = SyncNotes(
            cache = noteCache,
            service = noteService
        )

        // Confirm the cache is empty before any use-cases have been executed
        var cachedNotes = noteCache.fetchAll()
        assert(cachedNotes.isEmpty())

        // Execute the use-case
        val emissions = syncNotes.execute(
            token = token,
            isNetworkAvailable = true
        ).toList()

        // First emission should be loading
        assert(emissions[0] == DataState.Loading<List<Note>>(ProgressBarState.Loading))

        // Confirm second emission is data
        assert(emissions[1] is DataState.Data)
        assert((emissions[1] as DataState.Data).data?.size ?: 0 == NUM_NOTES)

        // Confirm the cache is no longer empty
        cachedNotes = noteCache.fetchAll()
        assert(cachedNotes.size == NUM_NOTES)

        // Confirm loading state is IDLE
        assert(emissions[2] == DataState.Loading<List<Note>>(ProgressBarState.Idle))
    }

    /**
     * 1. Insert some data into the cache by executing a successful use-case.
     * 2. Configure the network operation to return malformed data.
     * 3. Execute use-case for a second time and confirm it still emits the cached data.
     */
    @Test
    fun getNotes_malformedData_successFromCache() = runBlocking {
        // setup
        val noteDatabase = NoteDatabaseFake()
        val noteCache = NoteCacheFake(noteDatabase)
        val noteService = NoteServiceListFake.build(
            type = NoteServiceResponseTypeList.MalformedData // Malformed Data
        )

        syncNotes = SyncNotes(
            cache = noteCache,
            service = noteService
        )

        // Confirm the cache is empty before any use-cases have been executed
        var cachedNotes = noteCache.fetchAll()
        assert(cachedNotes.isEmpty())

        // Add some data to the cache by executing a successful request
        val noteData = serializeNoteData(NoteDataValid.listData)
        noteCache.insert(noteData)

        // Confirm the cache is not empty anymore
        cachedNotes = noteCache.fetchAll()
        assert(cachedNotes.size == NUM_NOTES)

        // Execute the use-case
        val emissions = syncNotes.execute(
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
        assert((emissions[2] as DataState.Data).data?.size == NUM_NOTES)

        // Confirm the cache is still not empty
        cachedNotes = noteCache.fetchAll()
        assert(cachedNotes.size == NUM_NOTES)

        // Confirm loading state is IDLE
        assert(emissions[3] == DataState.Loading<List<Note>>(ProgressBarState.Idle))
    }

    @Test
    fun getNotes_emptyList() = runBlocking {
        // setup
        val noteDatabase = NoteDatabaseFake()
        val noteCache = NoteCacheFake(noteDatabase)
        val noteService = NoteServiceListFake.build(
            type = NoteServiceResponseTypeList.EmptyList // Empty List
        )

        syncNotes = SyncNotes(
            cache = noteCache,
            service = noteService
        )

        // Confirm the cache is empty before any use-cases have been executed
        var cachedNotes = noteCache.fetchAll()
        assert(cachedNotes.isEmpty())

        // Execute the use-case
        val emissions = syncNotes.execute(
            token = token,
            isNetworkAvailable = true
        ).toList()

        // First emission should be loading
        assert(emissions[0] == DataState.Loading<List<Note>>(ProgressBarState.Loading))

        // Confirm second emission is data (empty list)
        assert(emissions[1] is DataState.Data)
        assert((emissions[1] as DataState.Data).data?.size ?: 0 == 0)

        // Confirm the cache is STILL EMPTY
        cachedNotes = noteCache.fetchAll()
        assert(cachedNotes.isEmpty())

        // Confirm loading state is IDLE
        assert(emissions[2] == DataState.Loading<List<Note>>(ProgressBarState.Idle))
    }

}