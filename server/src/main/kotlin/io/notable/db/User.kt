package io.notable.db

import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime
import java.util.UUID

private const val VAR_CHAR_LENGTH = 255

object UserTable : UUIDTable() {
    val email = varchar("email", VAR_CHAR_LENGTH).uniqueIndex()
    val password = varchar("password", VAR_CHAR_LENGTH)
    val salt = varchar("hash", VAR_CHAR_LENGTH)
    val createdAt = datetime("createdAt").default(LocalDateTime.now())
}

class User(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<User>(UserTable)

    var email by UserTable.email
    var password by UserTable.password
    var salt by UserTable.salt
}
