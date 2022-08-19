package io.notable.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.notable.constants.ErrorResponse
import io.notable.dto.errors.*

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<AuthenticationException> { call, _ ->
            call.respond(HttpStatusCode.Unauthorized)
        }

        exception<AuthorizationException> { call, _ ->
            call.respond(HttpStatusCode.Forbidden)
        }

        exception<ValidationException> { call, cause ->
            call.respond(
                HttpStatusCode.UnprocessableEntity,
                Errors(listOf(Errors.FormError(cause.params.toString(), cause.params.toString())))
            )
        }

        exception<UserDoesNotExists> { call, _ ->
            call.respond(HttpStatusCode.NotFound)
        }

        exception<BadRequestException> { call, cause ->
            call.respond(HttpStatusCode.BadRequest, ErrorDto(cause.message.toString(), HttpStatusCode.BadRequest.value))
            throw cause
        }

        exception<BadCredentialsException> { call, _ ->
            call.respond(
                HttpStatusCode.NotFound,
                Errors(
                    listOf(
                        Errors.FormError("email", ErrorResponse.invalidCredentials)
                    )
                )
            )
        }

        exception<EmailAlreadyTakenException> { call, _ ->
            call.respond(
                HttpStatusCode.Forbidden,
                Errors(
                    listOf(
                        Errors.FormError("email", ErrorResponse.emailAlreadyTaken)
                    )
                )
            )
        }

        exception<NoteNotFoundException> { call, _ ->
            call.respond(HttpStatusCode.NotFound)
        }
    }
}
