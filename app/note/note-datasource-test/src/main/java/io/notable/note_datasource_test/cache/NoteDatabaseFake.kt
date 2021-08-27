package io.notable.note_datasource_test.cache

import io.notable.note_domain.Note

class NoteDatabaseFake {
    val notes: MutableList<Note> = mutableListOf()
}