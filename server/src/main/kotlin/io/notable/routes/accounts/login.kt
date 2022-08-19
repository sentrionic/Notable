package io.notable.routes.accounts

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.resources.post
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.notable.services.UserService

fun Route.login(
    userService: UserService
) {
    post<Accounts.Login> {
        val request = parseAndValidateAuthRequest(call) ?: run {
            return@post
        }

        val response = userService.login(request)

        call.respond(status = HttpStatusCode.Created, message = response)
    }
}
