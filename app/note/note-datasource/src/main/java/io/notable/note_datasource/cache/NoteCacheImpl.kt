package io.notable.note_datasource.cache

import io.notable.note_datasource.cache.model.toNote
import io.notable.note_domain.Note
import io.notable.notedatasources.cache.NoteDbQueries

class NoteCacheImpl(
    private val noteDatabase: NoteDatabase,
) : NoteCache {

    private var queries: NoteDbQueries = noteDatabase.noteDbQueries

    override fun insert(note: Note) {
        return note.run {
            queries.insertNote(
                id = id,
                title = title,
                body = body,
                created_at = createdAt,
                updated_at = updatedAt
            )
        }
    }

    override fun insert(notes: List<Note>) {
        for (note in notes) {
            insert(note)
        }
    }

    override fun searchNotes(query: String): List<Note> {
        return queries.searchNotes(query).executeAsList().map { it.toNote() }
    }

    override fun fetchAll(): List<Note> {
        return queries.fetchAll().executeAsList().map { it.toNote() }
    }

    override fun getNote(noteId: String): Note {
        return queries.getNoteById(noteId).executeAsOne().toNote()
    }

    override fun removeNote(id: String) {
        return queries.removeNote(id)
    }

    override fun updateNote(id: String, title: String, body: String, updatedAt: String) {
        queries.updateNote(title = title, body = body, id = id, updatedAt = updatedAt)
    }

    override fun clearCache() {
        return queries.clearDatabase()
    }

    override fun deleteNotes(ids: List<String>) {
        queries.deleteNotes(ids)
    }

}