package io.notable.routes.notes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.resources.put
import io.notable.plugins.userId
import io.notable.services.NoteService

fun Route.editNote(
    noteService: NoteService,
) {
    put<Notes.Id> {route ->
        val request = parseAndValidateNoteRequest(call) ?: run {
            return@put
        }

        val response = noteService.editNote(request, route.id, call.userId())

        call.respond(status = HttpStatusCode.OK, message = response)
    }
}
