package io.notable.note_interactors

import com.squareup.sqldelight.db.SqlDriver
import io.notable.note_datasource.cache.NoteCache
import io.notable.note_datasource.network.NoteService

data class NoteInteractors(
    val syncNotes: SyncNotes,
    val getNoteFromCache: GetNoteFromCache,
    val filterNotes: FilterNotes,
    val deleteNote: DeleteNote,
    val createNote: CreateNote,
    val updateNote: UpdateNote,
    val clearDatabase: ClearDatabase,
    val syncDeletedNotes: SyncDeletedNotes
) {
    companion object Factory {
        fun build(sqlDriver: SqlDriver): NoteInteractors {
            val service = NoteService.build()
            val cache = NoteCache.build(sqlDriver)
            return NoteInteractors(
                syncNotes = SyncNotes(
                    service = service,
                    cache = cache
                ),
                getNoteFromCache = GetNoteFromCache(
                    cache = cache,
                ),
                filterNotes = FilterNotes(),
                deleteNote = DeleteNote(
                    service = service,
                    cache = cache,
                ),
                createNote = CreateNote(
                    service = service,
                    cache = cache,
                ),
                updateNote = UpdateNote(
                    service = service,
                    cache = cache,
                ),
                clearDatabase = ClearDatabase(
                    cache = cache
                ),
                syncDeletedNotes = SyncDeletedNotes(
                    service = service,
                    cache = cache,
                )
            )
        }

        val schema: SqlDriver.Schema = NoteCache.schema

        val dbName: String = NoteCache.dbName
    }
}