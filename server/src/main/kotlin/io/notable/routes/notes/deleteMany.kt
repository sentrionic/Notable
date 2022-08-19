package io.notable.routes.notes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.notable.dto.note.DeleteNotesRequest
import io.notable.plugins.userId
import io.notable.services.NoteService

fun Route.deleteNotes(
    noteService: NoteService
) {
    delete<Notes> {
        val request = call.receiveOrNull<DeleteNotesRequest>() ?: kotlin.run {
            call.respond(HttpStatusCode.BadRequest)
            return@delete
        }

        val response = noteService.deleteNotes(request, call.userId())

        call.respond(status = HttpStatusCode.OK, message = response)
    }
}
