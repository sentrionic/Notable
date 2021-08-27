package io.notable.note_datasource_test.cache

import io.notable.note_datasource.cache.NoteCache
import io.notable.note_domain.Note

class NoteCacheFake(
    private val db: NoteDatabaseFake
) : NoteCache {
    override fun insert(note: Note) {
        if (db.notes.isNotEmpty()) {
            var didInsert = false
            for (h in db.notes) {
                if (h.id == note.id) {
                    db.notes.remove(h)
                    db.notes.add(note)
                    didInsert = true
                    break
                }
            }
            if (!didInsert) {
                db.notes.add(note)
            }
        } else {
            db.notes.add(note)
        }
    }

    override fun insert(notes: List<Note>) {
        if (db.notes.isNotEmpty()) {
            for (note in notes) {
                if (db.notes.contains(note)) {
                    db.notes.remove(note)
                    db.notes.add(note)
                }
            }
        } else {
            db.notes.addAll(notes)
        }
    }

    override fun searchNotes(query: String): List<Note> {
        return db.notes.find {
            it.title.lowercase().contains(query.lowercase())
                    || it.body.lowercase().contains(query.lowercase())
        }?.let { listOf(it) } ?: listOf()
    }

    override fun fetchAll(): List<Note> {
        return db.notes
    }

    override fun getNote(noteId: String): Note? {
        return db.notes.find { it.id == noteId }
    }

    override fun removeNote(id: String) {
        for (i in 0..db.notes.size) {
            if (db.notes[i].id == id) {
                db.notes.removeAt(i)
                break
            }
        }
    }

    override fun updateNote(id: String, title: String, body: String, updatedAt: String) {
        for (note in db.notes) {
            if (note.id == id) {
                db.notes.remove(note)
                val updated = note.copy(title = title, body = body)
                db.notes.add(updated)
                break
            }
        }
    }

    override fun clearCache() {
        db.notes.clear()
    }

    override fun deleteNotes(ids: List<String>) {
        val toBeRemoved = mutableListOf<Note>()
        for (note in db.notes) {
            if (note.id in ids) {
                toBeRemoved.add(note)
            }
        }
        db.notes.removeAll(toBeRemoved)
    }

}