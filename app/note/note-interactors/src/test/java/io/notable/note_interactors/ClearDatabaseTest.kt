package io.notable.note_interactors

import io.notable.constants.SuccessHandling
import io.notable.core.domain.DataState
import io.notable.core.domain.ProgressBarState
import io.notable.core.domain.UIComponent
import io.notable.note_datasource_test.cache.NoteCacheFake
import io.notable.note_datasource_test.cache.NoteDatabaseFake
import io.notable.note_datasource_test.network.data.NoteDataValid
import io.notable.note_datasource_test.network.serializeNoteData
import io.notable.note_domain.Note
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Test

class ClearDatabaseTest {

    // system in test
    private lateinit var clearDatabase: ClearDatabase

    @Test
    fun clearDatabase_success() = runBlocking {
        // setup
        val noteDatabase = NoteDatabaseFake()
        val noteCache = NoteCacheFake(noteDatabase)

        clearDatabase = ClearDatabase(noteCache)

        // insert notes into the cache
        val noteData = serializeNoteData(NoteDataValid.listData)
        noteCache.insert(noteData)

        // assert cache is not empty
        val notes = noteCache.fetchAll()
        assert(notes.isNotEmpty())

        // Execute the use-case
        val emissions = clearDatabase.execute().toList()

        // First emission should be loading
        assert(emissions[0] == DataState.Loading<Note>(ProgressBarState.Loading))

        // Confirm second emission is the response
        assert(emissions[1] is DataState.Response)
        assert((emissions[1] as DataState.Response).uiComponent is UIComponent.None)
        assert(((emissions[1] as DataState.Response).uiComponent as UIComponent.None).message == SuccessHandling.DATABASE_CLEARED)

        // Confirm loading state is IDLE
        assert(emissions[2] == DataState.Loading<Note>(ProgressBarState.Idle))
    }
}
