package io.notable.note_interactors

import io.notable.core.domain.FilterOrder
import io.notable.note_datasource_test.network.data.NoteDataValid
import io.notable.note_datasource_test.network.serializeNoteData
import io.notable.note_domain.NoteFilter
import org.junit.Test

/**
 * 1. Success (Search for specific note by 'title' param)
 * 2. Success (Order by 'title' param DESC)
 * 3. Success (Order by 'title' param ASC)
 * 4. Success (Order by 'last updated' % ASC)
 * 5. Success (Order by 'last updated' % DESC)
 */
class FilterNotesTest {

    // System in test
    private lateinit var filterNotes: FilterNotes

    // Data
    private val noteList = serializeNoteData(NoteDataValid.listData)

    @Test
    fun searchNoteByTitle() {
        filterNotes = FilterNotes()

        val query = "Untitled #20"

        // Execute use-case
        val emissions = filterNotes.execute(
            current = noteList,
            query = query,
            noteFilter = NoteFilter.Title(),
        )

        // confirm it returns a single note
        assert(emissions[0].title == query)
    }

    @Test
    fun orderByTitleDesc() {
        filterNotes = FilterNotes()

        // Execute use-case
        val emissions = filterNotes.execute(
            current = noteList,
            query = "",
            noteFilter = NoteFilter.Title(order = FilterOrder.Descending),
        )

        // confirm they are ordered Z-A
        for (index in 1 until emissions.size) {
            assert(emissions[index - 1].title.toCharArray()[0] >= emissions[index].title.toCharArray()[0])
        }
    }

    @Test
    fun orderByTitleAsc() {
        filterNotes = FilterNotes()

        // Execute use-case
        val emissions = filterNotes.execute(
            current = noteList,
            query = "",
            noteFilter = NoteFilter.Title(order = FilterOrder.Ascending),
        )

        // confirm they are ordered A-Z
        for (index in 1 until emissions.size) {
            assert(emissions[index - 1].title.toCharArray()[0] <= emissions[index].title.toCharArray()[0])
        }
    }

    @Test
    fun orderByProWinsDesc() {
        filterNotes = FilterNotes()

        // Execute use-case
        val emissions = filterNotes.execute(
            current = noteList,
            query = "",
            noteFilter = NoteFilter.UpdatedAt(order = FilterOrder.Descending),
        )

        // confirm they are ordered highest to lowest
        for (index in 1 until emissions.size) {
            assert(emissions[index - 1].updatedAt.toCharArray()[0] >= emissions[index].updatedAt.toCharArray()[0])
        }
    }

    @Test
    fun orderByProWinsAsc() {
        filterNotes = FilterNotes()

        // Execute use-case
        val emissions = filterNotes.execute(
            current = noteList,
            query = "",
            noteFilter = NoteFilter.UpdatedAt(order = FilterOrder.Ascending),
        )

        // confirm they are ordered lowest to highest
        for (index in 1 until emissions.size) {
            assert(emissions[index - 1].updatedAt.toCharArray()[0] <= emissions[index].updatedAt.toCharArray()[0])
        }
    }
}
