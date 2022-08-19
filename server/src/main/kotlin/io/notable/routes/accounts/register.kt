package io.notable.routes.accounts

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.notable.dto.errors.EmailAlreadyTakenException
import io.notable.services.UserService
import org.jetbrains.exposed.exceptions.ExposedSQLException

fun Route.register(
    userService: UserService
) {
    post<Accounts> {
        val request = parseAndValidateAuthRequest(call) ?: run {
            return@post
        }

        try {
            val response = userService.register(request)
            call.respond(status = HttpStatusCode.Created, message = response)
        } catch (e: ExposedSQLException) {
            throw EmailAlreadyTakenException()
        }
    }
}
