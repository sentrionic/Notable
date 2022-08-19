package io.notable.routes.notes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.notable.plugins.userId
import io.notable.services.NoteService

fun Route.deleteNote(
    noteService: NoteService
) {
    delete<Notes.Id> { route ->
        val response = noteService.deleteNote(route.id, call.userId())
        call.respond(status = HttpStatusCode.OK, message = response)
    }
}
