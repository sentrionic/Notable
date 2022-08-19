package io.notable.services

import io.ktor.server.plugins.*
import io.notable.db.DatabaseFactory.dbQuery
import io.notable.db.Note
import io.notable.db.NoteTable
import io.notable.db.User
import io.notable.dto.errors.*
import io.notable.dto.note.DeleteNotesRequest
import io.notable.dto.note.NoteRequest
import io.notable.dto.note.NoteResponse
import kotlinx.datetime.toKotlinLocalDateTime
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import java.lang.IllegalArgumentException
import java.util.UUID

interface NoteService {
    suspend fun createNote(request: NoteRequest, userId: UUID): NoteResponse
    suspend fun getNotes(search: String, userId: UUID): List<NoteResponse>
    suspend fun editNote(request: NoteRequest, noteId: UUID, userId: UUID): NoteResponse
    suspend fun deleteNote(noteId: UUID, userId: UUID): NoteResponse
    suspend fun deleteNotes(request: DeleteNotesRequest, userId: UUID)
    suspend fun getDeletedNotes(userId: UUID): List<NoteResponse>
}

class NoteServiceImpl : NoteService {
    override suspend fun createNote(request: NoteRequest, userId: UUID): NoteResponse {
        return dbQuery {
            val author = User.findById(userId) ?: throw UserDoesNotExists()

            val note = Note.new {
                title = request.title
                body = request.body
                user = author.id
            }

            serializeNote(note)
        }
    }

    override suspend fun getNotes(search: String, userId: UUID): List<NoteResponse> {
        return dbQuery {
            val query = "%$search%"
            val notes = Note.find {
                (NoteTable.user eq userId) and
                    (NoteTable.deletedAt.isNull()) and
                    ((NoteTable.title like query) or (NoteTable.body like query))
            }.orderBy(NoteTable.updatedAt to SortOrder.DESC)

            notes.map { serializeNote(it) }
        }
    }

    override suspend fun editNote(request: NoteRequest, noteId: UUID, userId: UUID): NoteResponse {
        return dbQuery {
            val author = User.findById(userId) ?: throw UserDoesNotExists()

            val note = Note.findById(noteId) ?: throw NoteNotFoundException()

            if (note.user != author.id) throw AuthorizationException()

            note.apply {
                title = request.title
                body = request.body
                updatedAt = java.time.LocalDateTime.now()
            }

            serializeNote(note)
        }
    }

    override suspend fun deleteNote(noteId: UUID, userId: UUID): NoteResponse {
        return dbQuery {
            val author = User.findById(userId) ?: throw UserDoesNotExists()

            val note = Note.findById(noteId) ?: throw NoteNotFoundException()

            if (note.user != author.id) throw AuthorizationException()

            note.apply {
                deletedAt = java.time.LocalDateTime.now()
            }

            serializeNote(note)
        }
    }

    override suspend fun deleteNotes(request: DeleteNotesRequest, userId: UUID) {
        return dbQuery {
            val author = User.findById(userId) ?: throw UserDoesNotExists()

            val ids = try {
                request.ids.map { UUID.fromString(it) }
            } catch (e: IllegalArgumentException) {
                throw BadRequestException("Invalid UUID")
            }

            val notes = Note.find { NoteTable.id inList ids }

            for (note in notes) {
                if (note.user != author.id) throw AuthorizationException()
                note.deletedAt = java.time.LocalDateTime.now()
            }
        }
    }

    override suspend fun getDeletedNotes(userId: UUID): List<NoteResponse> {
        return dbQuery {
            val notes = Note.find {
                (NoteTable.user eq userId) and (NoteTable.deletedAt.isNotNull())
            }

            notes.map { serializeNote(it) }
        }
    }

    private fun serializeNote(note: Note) = NoteResponse(
        id = note.id.toString(),
        title = note.title,
        body = note.body,
        createdAt = note.createdAt.toKotlinLocalDateTime(),
        updatedAt = note.updatedAt.toKotlinLocalDateTime(),
        isDeleted = note.deletedAt != null
    )
}
