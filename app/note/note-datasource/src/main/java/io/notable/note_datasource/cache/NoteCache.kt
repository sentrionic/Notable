package io.notable.note_datasource.cache

import com.squareup.sqldelight.db.SqlDriver
import io.notable.note_domain.Note

interface NoteCache {

    fun insert(note: Note)

    fun insert(notes: List<Note>)

    fun searchNotes(query: String): List<Note>

    fun fetchAll(): List<Note>

    fun getNote(noteId: String): Note?

    fun removeNote(id: String)

    fun updateNote(id: String, title: String, body: String, updatedAt: String)

    fun clearCache()

    fun deleteNotes(ids: List<String>)

    companion object Factory {
        fun build(sqlDriver: SqlDriver): NoteCache {
            return NoteCacheImpl(NoteDatabase(sqlDriver))
        }

        val schema: SqlDriver.Schema = NoteDatabase.Schema

        val dbName: String = "notes.db"
    }
}