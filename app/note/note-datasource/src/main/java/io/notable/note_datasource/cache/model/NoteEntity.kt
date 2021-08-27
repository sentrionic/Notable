package io.notable.note_datasource.cache.model

import io.notable.note_domain.Note
import io.notable.notedatasources.cache.NoteEntity

fun NoteEntity.toNote(): Note {
    return Note(
        id = id,
        title = title,
        body = body,
        createdAt = created_at,
        updatedAt = updated_at
    )
}