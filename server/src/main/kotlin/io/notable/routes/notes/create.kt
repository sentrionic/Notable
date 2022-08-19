package io.notable.routes.notes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.notable.plugins.userId
import io.notable.services.NoteService

fun Route.createNote(
    noteService: NoteService
) {
    post<Notes> {
        val request = parseAndValidateNoteRequest(call) ?: run {
            return@post
        }

        val response = noteService.createNote(request, call.userId())

        call.respond(status = HttpStatusCode.Created, message = response)
    }
}
