package io.notable.routes.notes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.routing.get
import io.notable.plugins.userId
import io.notable.services.NoteService

fun Route.listNotes(
    noteService: NoteService
) {
    get<Notes> { route ->
        val response = noteService.getNotes(route.search, call.userId())
        call.respond(status = HttpStatusCode.OK, message = response)
    }
}
