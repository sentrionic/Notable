package io.notable.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.resources.*
import io.ktor.server.routing.*
import io.notable.constants.AppConstants
import io.notable.dto.errors.AuthenticationException
import io.notable.routes.accounts.login
import io.notable.routes.accounts.register
import io.notable.routes.notes.*
import io.notable.services.NoteService
import io.notable.services.UserService
import org.koin.ktor.ext.inject
import java.util.UUID

fun Application.configureRouting() {
    val userService: UserService by inject()
    val noteService: NoteService by inject()

    install(Resources)
    val root = routing {
        register(userService)
        login(userService)

        authenticate {
            createNote(noteService)
            editNote(noteService)
            deleteNote(noteService)
            deleteNotes(noteService)
            listDeleted(noteService)
            listNotes(noteService)
        }
    }
    val allRoutes = allRoutes(root)
    val allRoutesWithMethod = allRoutes.filter { it.selector is HttpMethodRouteSelector }
    allRoutesWithMethod.forEach {
        println("route: $it")
    }
}

fun allRoutes(root: Route): List<Route> {
    return listOf(root) + root.children.flatMap { allRoutes(it) }
}

fun ApplicationCall.userId(): UUID {
    val principal = principal<JWTPrincipal>() ?: throw AuthenticationException()
    val claim = principal.getClaim(AppConstants.userId, String::class) ?: throw AuthenticationException()
    return UUID.fromString(claim)
}
