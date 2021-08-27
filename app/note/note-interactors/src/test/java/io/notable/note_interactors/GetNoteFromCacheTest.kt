package io.notable.note_interactors

import io.notable.constants.ErrorHandling
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
import kotlin.random.Random

/**
 * 1. Success (Retrieve a note from the cache successfully)
 * 2. Failure (The note does not exist in the cache)
 */
class GetNoteFromCacheTest {

    // system in test
    private lateinit var getNoteFromCache: GetNoteFromCache

    @Test
    fun getNoteFromCache_success() = runBlocking {
        // setup
        val noteDatabase = NoteDatabaseFake()
        val noteCache = NoteCacheFake(noteDatabase)

        getNoteFromCache = GetNoteFromCache(noteCache)

        // insert notes into the cache
        val noteData = serializeNoteData(NoteDataValid.listData)
        noteCache.insert(noteData)

        // choose a note at random
        val note = noteData[Random.nextInt(0, noteData.size - 1)]

        // Execute the use-case
        val emissions = getNoteFromCache.execute(note.id).toList()

        // First emission should be loading
        assert(emissions[0] == DataState.Loading<Note>(ProgressBarState.Loading))

        // Confirm second emission is data from the cache
        assert(emissions[1] is DataState.Data)
        assert((emissions[1] as DataState.Data).data?.id == note.id)
        assert((emissions[1] as DataState.Data).data?.title == note.title)

        // Confirm loading state is IDLE
        assert(emissions[2] == DataState.Loading<Note>(ProgressBarState.Idle))
    }

    @Test
    fun getNoteFromCache_fail() = runBlocking {
        // setup
        val noteDatabase = NoteDatabaseFake()
        val noteCache = NoteCacheFake(noteDatabase)

        getNoteFromCache = GetNoteFromCache(noteCache)

        // insert notes into the cache
        val noteData = serializeNoteData(NoteDataValid.listData)
        noteCache.insert(noteData)

        // choose a note at random and remove it from the cache
        val note = noteData[Random.nextInt(0, noteData.size - 1)]
        noteCache.removeNote(note.id)

        // Execute the use-case
        val emissions = getNoteFromCache.execute(note.id).toList()

        // First emission should be loading
        assert(emissions[0] == DataState.Loading<Note>(ProgressBarState.Loading))

        // Confirm second emission is error response
        assert(emissions[1] is DataState.Response)
        assert(((emissions[1] as DataState.Response).uiComponent as UIComponent.Dialog).title == "Error")
        assert(
            ((emissions[1] as DataState.Response).uiComponent as UIComponent.Dialog).description == ErrorHandling.NOTE_DOES_NOT_EXIST
        )

        // Confirm loading state is IDLE
        assert(emissions[2] == DataState.Loading<Note>(ProgressBarState.Idle))
    }
}
