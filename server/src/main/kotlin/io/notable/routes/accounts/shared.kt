@file:Suppress("MatchingDeclarationName")

package io.notable.routes.accounts

import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.notable.constants.RouteConstants
import io.notable.dto.auth.AuthRequest
import io.notable.dto.errors.formatErrors
import io.notable.validation.authValidator
import kotlinx.serialization.Serializable

@Serializable
@Resource(RouteConstants.accountRoute)
class Accounts {

    @Serializable
    @Resource(RouteConstants.loginRoute)
    class Login(val parent: Accounts = Accounts())
}

suspend fun parseAndValidateAuthRequest(call: ApplicationCall): AuthRequest? {
    val request = call.receiveOrNull<AuthRequest>() ?: run {
        call.respond(HttpStatusCode.BadRequest)
        return null
    }

    val violations = authValidator.validate(request)
    if (!violations.isValid) {
        call.respond(HttpStatusCode.BadRequest, formatErrors(violations))
        return null
    }

    return request
}
