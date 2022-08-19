package io.notable.db

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime
import java.util.*

private const val VAR_CHAR_LENGTH = 255

object NoteTable : UUIDTable() {
    val title = varchar("title", VAR_CHAR_LENGTH)
    val body = text("body").nullable()
    val createdAt = datetime("createdAt").default(LocalDateTime.now())
    val updatedAt = datetime("updatedAt").default(LocalDateTime.now())
    val deletedAt = datetime("deletedAt").nullable()
    val user = reference("userId", UserTable)
}

class Note(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Note>(NoteTable)

    var title by NoteTable.title
    var body by NoteTable.body
    val createdAt by NoteTable.createdAt
    var updatedAt by NoteTable.updatedAt
    var deletedAt by NoteTable.deletedAt
    var user by NoteTable.user
}
